package com.gotravel.mobile.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.gotravel.mobile.data.model.Usuario
import java.io.DataInputStream
import java.io.DataOutputStream
import java.math.BigInteger
import java.net.Socket
import java.security.MessageDigest
import java.time.format.DateTimeFormatter

// Valores por defecto que se cambian al iniciar sesion
object AppUiState {
    lateinit var socket: Socket
    lateinit var usuario: Usuario
    lateinit var entrada: DataInputStream
    lateinit var salida: DataOutputStream
}

object Regex {
    val regexEmail = "^(?=.{1,150}\$)[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    val regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).{8,}$".toRegex()
    val regexNombre = "^(?=.{1,45}\$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexCamposGrandes = "^(?=.{1,200}\$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexTfno = "^\\d{9}$".toRegex()
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

@RequiresApi(Build.VERSION_CODES.O)
val formatoFromDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
@RequiresApi(Build.VERSION_CODES.O)
val formatoFinal = DateTimeFormatter.ofPattern("dd/MM/yyyy")