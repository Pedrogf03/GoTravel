package com.gotravel.mobile.data

import android.content.Context
import com.gotravel.mobile.network.ImageApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface AppContainer {
    val ApiRepository: AppRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = "https://api.api-ninjas.com/v1/"
    private val apiKey = "mvJLckP+ODVVNtwGqUupVw==FY5JmodyfUIlt5QX"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                .header("X-Api-Key", apiKey)
                .header("Accept", "image/jpg")
                .method(original.method, original.body)

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()


    private val retrofitService: ImageApiService by lazy {
        retrofit.create(ImageApiService::class.java)
    }

    override val ApiRepository: AppRepository by lazy {
        NetworkRepository(retrofitService)
    }

}