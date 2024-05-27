package com.gotravel.mobile.ui.utils
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.gotravel.mobile.data.model.Pago
import com.gotravel.mobile.data.model.Suscripcion
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
import java.time.LocalDate

class PayPalSubscriptions(
    private val context: Context
) {

    private val client = OkHttpClient.Builder().build()
    private val planId = "P-7EA59780H1130000JMZJTC5A"
    private val tokenClient = PayPalToken()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createSubscription() {
        val url = "https://api-m.sandbox.paypal.com/v1/billing/subscriptions"

        val token = tokenClient.obtenerTokenPaypal()

        val mediaType = "application/json".toMediaType()

        val json = """
            {
              "plan_id": "$planId",
              "quantity": "1",
              "auto_renewal": true,
              "subscriber": {
                "name": {
                  "given_name": "${AppUiState.usuario.nombre}",
                  "surname": "${if(AppUiState.usuario.apellidos != null) AppUiState.usuario.apellidos else ""}"
                },
                "email_address": "${AppUiState.usuario.email}"
              },
              "application_context": {
                "brand_name": "GoTravel!",
                "locale": "es-ES",
                "shipping_preference": "NO_SHIPPING",
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSuscription(subscriptionId: String): Suscripcion? {
        val url = "https://api-m.sandbox.paypal.com/v1/billing/subscriptions/$subscriptionId"

        val token = tokenClient.obtenerTokenPaypal()

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                println("Error getting sub: ${response.code}")
                println("Response body: ${response.body?.string()}")
                null
            } else {
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody!!)

                val estado = json.getString("status")
                val fechaInicio = json.getString("start_time")
                val billingInfo = json.getJSONObject("billing_info")
                val fechaFinal = LocalDate.parse(fechaInicio.take(10), formatoFromDb).plusMonths(1).format(formatoFromDb)
                val coste: Double = try {
                    billingInfo.getJSONObject("last_payment").getJSONObject("amount").getString("value").toDouble()
                } catch (e: NullPointerException) {
                    4.99
                }

                val pago = Pago(coste = coste, fecha = fechaInicio.take(10))
                return@withContext Suscripcion(id = subscriptionId, fechaInicio = fechaInicio.take(10), fechaFinal = fechaFinal, estado = estado,renovar = "1", pagos = listOf(pago))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cancelSubscription(
        subscriptionId: String,
        onSuscripcionCancelada: () -> Unit
        ) {
        val url = "https://api-m.sandbox.paypal.com/v1/billing/subscriptions/${subscriptionId}/suspend"

        val token = tokenClient.obtenerTokenPaypal()

        val mediaType = "application/json".toMediaType()

        val json = """
            {
              "reason": "Cancelar suscripción"
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

                        onSuscripcionCancelada()

                    }
                }

            })
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun activateSubscription(
        subscriptionId: String,
        onSuscripcionReactivada: () -> Unit
    ) {
        val url = "https://api-m.sandbox.paypal.com/v1/billing/subscriptions/${subscriptionId}/activate"

        val token = tokenClient.obtenerTokenPaypal()

        val mediaType = "application/json".toMediaType()

        val json = """
            {
              "reason": "Renovar suscripción"
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

                        onSuscripcionReactivada()

                    }
                }

            })
        }
    }

}
