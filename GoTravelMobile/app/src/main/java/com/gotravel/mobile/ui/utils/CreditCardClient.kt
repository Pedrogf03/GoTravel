package com.gotravel.mobile.ui.utils
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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

class CreditCardPaymentClient(
    private val context: Context
) {

    private val client = OkHttpClient.Builder().build()
    private val planId = "P-4TM537472W447740TMZILMPY"
    private val tokenClient = PayPalToken()

    @RequiresApi(Build.VERSION_CODES.O)
    fun createSubscription() {
        tokenClient.obtenerTokenPaypal { token ->
            subscription(token)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun subscription(token: String) {
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
                "value": "4.99"
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
                    "address_line_1": "2211 N First Street",
                    "address_line_2": "Building 17",
                    "admin_area_2": "San Jose",
                    "admin_area_1": "CA",
                    "postal_code": "95131",
                    "country_code": "US"
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
                    // La solicitud fue exitosa, puedes procesar la respuesta aquí
                    val responseBody = response.body?.string()

                    // Analiza la respuesta JSON
                    val json = JSONObject(responseBody)

                    // Extrae el enlace de aprobación
                    val links = json.getJSONArray("links")
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

        })
    }
}
