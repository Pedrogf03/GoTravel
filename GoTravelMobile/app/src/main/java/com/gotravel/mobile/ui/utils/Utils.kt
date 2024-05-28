package com.gotravel.mobile.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Suscripcion
import com.gotravel.mobile.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.math.BigInteger
import java.net.Socket
import java.security.MessageDigest
import java.time.format.DateTimeFormatter

// Valores por defecto que se cambian al iniciar sesion
object AppUiState {
    var segundoPlano: Boolean = false
    var socket: Socket? = null
    lateinit var usuario: Usuario
    lateinit var entrada: DataInputStream
    lateinit var salida: DataOutputStream
}

object Regex {
    val regexEmail = "^(?=.{1,150}\$)[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    val regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).{8,}$".toRegex()
    val regexNombre = "^(?=.{1,45}\$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexApellidos = "^(?=.{1,200}\$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexCamposGrandes = "^(?=.{1,500}\$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexTfno = "^\\d{9}$".toRegex()
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

@RequiresApi(Build.VERSION_CODES.O)
val formatoFromDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
@RequiresApi(Build.VERSION_CODES.O)
val formatoFinal: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

suspend fun addRolProfesional(suscripcion: Suscripcion) {

    return withContext(Dispatchers.IO) {
        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        try {

            AppUiState.salida.writeUTF("suscripcion;crear;" + gson.toJson(suscripcion))

            val jsonFromServer = AppUiState.entrada.readUTF()
            val usuario : Usuario? = gson.fromJson(jsonFromServer, Usuario::class.java)

            if(usuario != null) {
                AppUiState.usuario = usuario.copy(foto = AppUiState.usuario.foto)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}




