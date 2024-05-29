package com.gotravel.mobile.data.model

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class Imagen (
    val id: Int,
    var imagen: ByteArray
) {
    val foto: ImageBitmap
        get() {
            return BitmapFactory.decodeByteArray(imagen, 0, imagen.size).asImageBitmap()
        }
}