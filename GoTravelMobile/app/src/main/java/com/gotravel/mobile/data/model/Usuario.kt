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
    var nombre: String,
    var apellidos: String? = null,
    var email: String,
    val contrasena: String,
    val roles: List<Rol>,
    var tfno: String? = null,
    var foto: ByteArray? = null
) {
    val imagen: ImageBitmap?
        get() {
            return foto?.let { BitmapFactory.decodeByteArray(foto, 0, it.size).asImageBitmap() }
        }

    val getApellidos : String
        get() {
            return if(apellidos == null) "" else apellidos!!
        }

    val getTfno : String
        get() {
            return if(tfno == null) "" else tfno!!
        }
}