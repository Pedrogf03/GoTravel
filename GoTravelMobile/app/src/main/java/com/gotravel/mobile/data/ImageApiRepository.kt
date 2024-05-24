package com.gotravel.mobile.data

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.gotravel.mobile.network.ImageApiService
import java.io.InputStream

interface AppRepository {
    suspend fun getLandingImage() : ImageBitmap
    suspend fun getHomeImage(): ImageBitmap
}

class NetworkRepository(private val service: ImageApiService) : AppRepository {
    override suspend fun getLandingImage(): ImageBitmap {

        val responseBody = service.getLandingImage()
        val inputStream: InputStream = responseBody.byteStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return bitmap.asImageBitmap()

    }

    override suspend fun getHomeImage(): ImageBitmap {

        val responseBody = service.getHomeImage()
        val inputStream: InputStream = responseBody.byteStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return bitmap.asImageBitmap()

    }
}