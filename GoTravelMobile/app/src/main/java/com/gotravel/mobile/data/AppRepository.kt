package com.gotravel.mobile.data

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.gotravel.mobile.network.AppService
import java.io.InputStream

interface AppRepository {
    suspend fun getImagen() : ImageBitmap
}

class NetworkRepository(private val service: AppService) : AppRepository {
    override suspend fun getImagen(): ImageBitmap {

        val responseBody = service.getImagen()
        val inputStream: InputStream = responseBody.byteStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return bitmap.asImageBitmap()

    }
}