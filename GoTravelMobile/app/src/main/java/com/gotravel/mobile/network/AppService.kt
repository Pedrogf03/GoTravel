package com.gotravel.mobile.network

import okhttp3.ResponseBody
import retrofit2.http.GET

interface AppService {
    @GET("randomimage?category=city&width=1250&height=1920")
    suspend fun getLandingImage(): ResponseBody

    @GET("randomimage?category=city&width=1920&height=1080")
    suspend fun getHomeImage(): ResponseBody

}