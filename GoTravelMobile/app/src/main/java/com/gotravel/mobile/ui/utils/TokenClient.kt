package com.gotravel.mobile.ui.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class PayPalToken {

    var accessToken: String? = null

    private val CLIENT_ID =
        "AfZIYHTA99Uktc2T4jpWKsXgQlY3HRx925HZTAHtyu0_Cyd-68zVelGEd2byWcIGjR_HX_25-ZOMk3VX"

    private val CLIENT_SECRET =
        "EExHjSx9dl1uC6WHdy7LzO9voSVnUMFfKQnYzyg-6uQsf88SWtqyW1WtOJ359s90hDFlUPJTkFj-1XaQ"

    suspend fun obtenerTokenPaypal(): String {
        val url = "https://api-m.sandbox.paypal.com/v1/oauth2/token"

        val client = OkHttpClient().newBuilder()
            .build()

        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val body = "grant_type=client_credentials".toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", Credentials.basic(CLIENT_ID, CLIENT_SECRET))
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Error: ${response.code}")
                    println("Response body: ${response.body?.string()}")
                    throw IOException("Unexpected code $response")
                } else {
                    val responseBody = response.body?.string()
                    val json = responseBody?.let { JSONObject(it) }
                    val tokenResponse = json?.getString("access_token")

                    // Guarda la información en variables
                    accessToken = tokenResponse

                    // Devuelve el token
                    return@withContext accessToken!!
                }
            }
        }
    }

}