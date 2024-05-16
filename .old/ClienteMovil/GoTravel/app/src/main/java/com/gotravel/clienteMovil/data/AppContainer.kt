package com.gotravel.clienteMovil.data

import android.content.Context
import com.gotravel.clienteMovil.network.Service
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import java.io.IOException
import java.util.Properties

interface AppContainer {
    val ApiRepository: Repository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl: String
        get() = getUrl()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: Service by lazy {
        retrofit.create(Service::class.java)
    }

    override val ApiRepository: Repository by lazy {
        NetworkRepository(retrofitService)
    }

    private fun getUrl(): String {
        var url = "http://"
        val conf = Properties()
        try {
            val inputStream = context.assets.open("client.properties")
            conf.load(inputStream)
            url += conf.getProperty("IP") + ":8080/"
        } catch (e: IOException) {
            println("No se ha podido leer el archivo de propiedades")
            url = "http://80.31.21.94:8080/"
        }
        return url
    }

}