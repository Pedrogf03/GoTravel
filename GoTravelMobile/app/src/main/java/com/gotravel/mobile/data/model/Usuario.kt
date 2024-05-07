package com.gotravel.mobile.data.model

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class Usuario(
    val id: Int?,
    val nombre: String,
    val apellidos: String? = null,
    val email: String,
    val contrasena: String,
    val roles: List<Rol>,
    val tfno: String? = null,
    val foto: ByteArray? = null
) {
    val imagen: ImageBitmap
        get() {
            return BitmapFactory.decodeByteArray(foto!!, 0, foto.size).asImageBitmap()
        }
}