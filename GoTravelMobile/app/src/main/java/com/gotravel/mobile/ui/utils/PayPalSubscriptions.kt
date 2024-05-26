package com.gotravel.mobile.ui.utils
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.gotravel.mobile.data.model.DirFacturacion
import com.gotravel.mobile.data.model.Pago
import com.gotravel.mobile.data.model.Suscripcion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.time.Instant

class PayPalSubscriptions(
    private val context: Context
) {

    private val client = OkHttpClient.Builder().build()
    private val planId = "P-7EA59780H1130000JMZJTC5A"
    private val tokenClient = PayPalToken()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createSubscription(dirFacturacion: DirFacturacion) {
        val token = tokenClient.obtenerTokenPaypal()
        subscription(token, dirFacturacion)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSuscription(id: String) : Suscripcion {
        val token = tokenClient.obtenerTokenPaypal()
        return getSubscriptionDetails(token = token, subscriptionId = id)!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun subscription(token: String, dirFacturacion: DirFacturacion) {
        val url = "https://api-m.sandbox.paypal.com/v1/billing/subscriptions"

        val mediaType = "application/json".toMediaType()
        val startTime = Instant.now().plusSeconds(3600).toString()
        val json = """
            {
              "plan_id": "$planId",
              "start_time": "$startTime",
              "quantity": "1",
              "shipping_amount": {
                "currency_code": "EUR",
                "value": "0.00"
              },
              "subscriber": {
                "name": {
                  "given_name": "${AppUiState.usuario.nombre}",
                  "surname": "${AppUiState.usuario.apellidos}"
                },
                "email_address": "${AppUiState.usuario.email}",
                "shipping_address": {
                  "name": {
                    "full_name": "${AppUiState.usuario.nombre} ${AppUiState.usuario.apellidos}"
                  },
                  "address": {
                    "address_line_1": "${dirFacturacion.linea1}",
                    "address_line_2": "${dirFacturacion.linea2}",
                    "admin_area_2": "${dirFacturacion.ciudad}",
                    "admin_area_1": "${dirFacturacion.estado}",
                    "postal_code": "${dirFacturacion.cp}",
                    "country_code": "${dirFacturacion.codigoPais}"
                  }
                }
              },
              "application_context": {
                "brand_name": "GoTravel!",
                "locale": "es-ES",
                "shipping_preference": "SET_PROVIDED_ADDRESS",
                "user_action": "SUBSCRIBE_NOW",
                "payment_method": {
                  "payer_selected": "PAYPAL",
                  "payee_preferred": "IMMEDIATE_PAYMENT_REQUIRED"
                },
                "return_url": "gotravel://returnurl",
                "cancel_url": "gotravel://cancelurl"
              }
            }
        """.trimIndent()
        val body = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Prefer", "return=representation")
            .build()

        withContext(Dispatchers.IO) {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Aquí puedes manejar el caso cuando la solicitud falla
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        println("Error: ${response.code}")
                        println("Response body: ${response.body?.string()}")
                    } else {

                        val responseBody = response.body?.string()
                        val json = responseBody?.let { JSONObject(it) }

                        // Extrae el enlace de aprobación
                        val links = json?.getJSONArray("links")
                        if (links != null) {
                            for (i in 0 until links.length()) {
                                val link = links.getJSONObject(i)
                                if (link.getString("rel") == "approve") {
                                    val approvalUrl = link.getString("href")

                                    // Crea un Intent para abrir el enlace de aprobación en el navegador
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(approvalUrl))
                                    context.startActivity(intent)
                                    break
                                }
                            }
                        }
                    }
                }

            })
        }
    }

    private suspend fun getSubscriptionDetails(subscriptionId: String, token: String): Suscripcion? {
        val url = "https://api-m.sandbox.paypal.com/v1/billing/subscriptions/$subscriptionId"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                println("Error: ${response.code}")
                println("Response body: ${response.body?.string()}")
                null
            } else {
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody!!)

                val estado = json.getString("status")
                val fechaInicio = json.getString("start_time")
                val billingInfo = json.getJSONObject("billing_info")
                val fechaFinal = billingInfo.getString("next_billing_time")
                val coste = billingInfo.getJSONObject("last_payment").getJSONObject("amount").getString("value").toDouble()

                val pago = Pago(coste = coste, fecha = fechaInicio.take(10))
                return@withContext Suscripcion(id = subscriptionId, fechaInicio = fechaInicio.take(10), fechaFinal = fechaFinal.take(10), estado = estado, pago = pago)
            }
        }
    }

}
