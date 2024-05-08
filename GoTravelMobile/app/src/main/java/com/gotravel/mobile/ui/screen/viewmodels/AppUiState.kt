package com.gotravel.mobile.ui.screen.viewmodels

import com.gotravel.mobile.data.model.Rol
import com.gotravel.mobile.data.model.Usuario
import java.math.BigInteger
import java.net.Socket
import java.security.MessageDigest

// Valores por defecto que se cambian al iniciar sesion
object AppUiState {
    var socket: Socket = Socket()
    var usuario: Usuario = Usuario(0, "", null, "", "", listOf(Rol("")))
}

object Regex {
    val regexEmail = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    val regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).{8,}$".toRegex()
    val regexNombre = "^[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexTfno = "^\\d{9}$".toRegex()
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}