package com.gotravel.mobile.ui.utils

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException


class PayPalToken {

    var accessToken: String? = null

    private val CLIENT_ID =
        "AfZIYHTA99Uktc2T4jpWKsXgQlY3HRx925HZTAHtyu0_Cyd-68zVelGEd2byWcIGjR_HX_25-ZOMk3VX"

    private val CLIENT_SECRET =
        "EExHjSx9dl1uC6WHdy7LzO9voSVnUMFfKQnYzyg-6uQsf88SWtqyW1WtOJ359s90hDFlUPJTkFj-1XaQ"

    fun obtenerTokenPaypal(onTokenObtained: (String) -> Unit) {
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

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                // Parsea la respuesta JSON a la clase TokenResponse
                val gson = Gson()
                val tokenResponse = gson.fromJson(response.body?.string(), TokenResponse::class.java)

                // Guarda la información en variables
                accessToken = tokenResponse.accessToken

                // Invoca el callback con el token
                onTokenObtained(accessToken!!)
            }
        })
    }
}


data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("app_id") val appId: String
)
