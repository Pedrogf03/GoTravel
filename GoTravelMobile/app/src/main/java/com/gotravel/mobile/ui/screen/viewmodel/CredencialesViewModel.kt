package com.gotravel.mobile.ui.screen.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.screen.CredencialesDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.math.BigInteger
import java.net.Socket
import java.security.MessageDigest
import java.util.Properties

class CredencialesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val opcion: String = checkNotNull(savedStateHandle[CredencialesDestination.opcion])

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    val regexEmail = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    val regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).{8,}$".toRegex()
    val regexNombre = "^[a-zA-Z0-9]*$".toRegex()

    private fun updateSocket(cliente: Socket) {
        GoTravelUiState.socket = cliente
    }

    private fun updateUsuario(usuario: Usuario) {
        GoTravelUiState.usuario = usuario
    }

    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    fun iniciarSesion(email: String, contrasena: String, context: Context) : Usuario? {

        mensajeUi.postValue("Conectando con el servidor...")

        if (!(email.isBlank() || email.isEmpty()) && !(contrasena.isBlank() || contrasena.isEmpty())) {

                var dirIp = "192.168.1.39"
                var puerto = 8484

                val conf = Properties()
                try {
                    val inputStream = context.openFileInput("client.properties")
                    conf.load(inputStream)
                    puerto = Integer.parseInt(conf.getProperty("PUERTO"))
                    dirIp = conf.getProperty("IP")
                } catch (e: IOException) {
                    println("No se ha podido leer el archivo de propiedades")
                }

                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                var usuario : Usuario? = null

                try {
                    val cliente = Socket(dirIp, puerto)
                    updateSocket(cliente)

                    val salida = DataOutputStream(cliente.getOutputStream())
                    val entrada = DataInputStream(cliente.getInputStream())

                    salida.writeUTF("login;$email;${contrasena.sha256()}")
                    salida.flush()

                    val jsonFromServer = entrada.readUTF()
                    usuario = gson.fromJson(jsonFromServer, Usuario::class.java)

                    if (usuario != null) {
                        mensajeUi.postValue("Sesión iniciada correctamente")
                    } else {
                        mensajeUi.postValue("Usuario o contraseña incorrectos")
                    }

                } catch (e: IOException) {
                    mensajeUi.postValue("No se ha podido conectar con el servidor")
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                updateUsuario(usuario!!)
                return usuario

        } else {
            mensajeUi.postValue("Por favor, rellena todos los campos")
        }

        return null

    }

    fun registrarse(email: String, contrasena: String, nombre: String,  confirmarContrasena: String, context: Context) : Usuario? {

        mensajeUi.postValue("Conectando con el servidor...")

        if (!(nombre.isBlank() || nombre.isEmpty()) && !(email.isBlank() || email.isEmpty()) && !(contrasena.isBlank() || contrasena.isEmpty()) && !(confirmarContrasena.isBlank() || confirmarContrasena.isEmpty())) {

            if (!nombre.matches(regexNombre)) {
                mensajeUi.postValue("El nombre no es válido")
                mensajeUi.postValue(nombre)
            } else if (!email.matches(regexEmail)) {
                mensajeUi.postValue("El email no es válido")
            } else if (!contrasena.matches(regexContrasena)) {
                mensajeUi.postValue("La contraseña no es válida")
            } else {

                val contrasenaHash = contrasena.sha256()
                val confirmarHash = confirmarContrasena.sha256()

                if(contrasenaHash != confirmarHash) {
                    mensajeUi.postValue("Las contraseñas no coinciden")
                } else {

                    var dirIp = "192.168.1.39"
                    var puerto = 8484

                    val conf = Properties()
                    try {
                        val inputStream = context.openFileInput("client.properties")
                        conf.load(inputStream)
                        puerto = Integer.parseInt(conf.getProperty("PUERTO"))
                        dirIp = conf.getProperty("IP")
                    } catch (e: IOException) {
                        println("No se ha podido leer el archivo de propiedades")
                    }

                    val gson = GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create()

                    var usuario : Usuario? = null

                    try {
                        val cliente = Socket(dirIp, puerto)
                        updateSocket(cliente)

                        val salida = DataOutputStream(cliente.getOutputStream())
                        val entrada = DataInputStream(cliente.getInputStream())

                        salida.writeUTF("registro;$email;$contrasenaHash;$nombre")
                        salida.flush()

                        val jsonFromServer = entrada.readUTF()
                        usuario = gson.fromJson(jsonFromServer, Usuario::class.java)

                        if (usuario != null) {
                            mensajeUi.postValue("Usuario registrado correctamente")
                        } else {
                            mensajeUi.postValue("Ese email ya está en uso")
                        }

                    } catch (e: IOException) {
                        mensajeUi.postValue("No se ha podido conectar con el servidor")
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    updateUsuario(usuario!!)
                    return usuario

                }

            }

        } else {
            mensajeUi.postValue("Por favor, rellena todos los campos")
        }

        return null

    }

}