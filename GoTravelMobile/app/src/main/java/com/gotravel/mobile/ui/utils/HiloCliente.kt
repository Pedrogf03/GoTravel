package com.gotravel.mobile.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class HiloCliente {

    fun reduceImageQuality(byteArray: ByteArray, quality: Int): ByteArray {
        // Decodifica el ByteArray a un Bitmap
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        // Crea un nuevo ByteArrayOutputStream
        val outputStream = ByteArrayOutputStream()

        // Comprime el Bitmap en el ByteArrayOutputStream
        // Utiliza un valor de calidad menor para reducir el tamaño del ByteArray resultante
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Convierte el ByteArrayOutputStream a un ByteArray
        return outputStream.toByteArray()
    }

}