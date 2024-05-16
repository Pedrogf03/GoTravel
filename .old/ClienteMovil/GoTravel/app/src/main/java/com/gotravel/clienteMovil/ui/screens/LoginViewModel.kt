package com.gotravel.clienteMovil.ui.screens

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.Properties

data class GoTravelUiState(
    val socket: Socket? = null
)

class LoginViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(GoTravelUiState())
    val uiState: StateFlow<GoTravelUiState> = _uiState.asStateFlow()

    val mensajeUi = MutableLiveData<String>()

    private fun updateSocket(cliente: Socket) {
        _uiState.update { currentState ->
            currentState.copy(socket = cliente)
        }
    }

    fun iniciarSesion(email: String, contrasena: String, context: Context) : Int? {

        var idUsuario : Int? = null

        if (!(email.isBlank() || email.isEmpty()) && !(contrasena.isBlank() || contrasena.isEmpty())) {
            var dirIp = "192.168.1.39"
            var puerto = 8484

            val conf = Properties()
            try {
                val inputStream = context.assets.open("client.properties")
                conf.load(inputStream)
                puerto = Integer.parseInt(conf.getProperty("PUERTO"))
                dirIp = conf.getProperty("IP")
            } catch (e: IOException) {
                println("No se ha podido leer el archivo de propiedades")
            }

            try {
                val cliente = Socket(dirIp, puerto)
                updateSocket(cliente)
                val entrada = DataInputStream(cliente.getInputStream())
                val salida = DataOutputStream(cliente.getOutputStream())

                salida.writeUTF("login;$email;$contrasena")
                val fromServer: List<String> = entrada.readUTF().split(";")

                if (fromServer[0] == "correcto") {
                    mensajeUi.postValue("Sesión iniciada correctamente")
                    idUsuario = fromServer[1].toInt()
                } else {
                    mensajeUi.postValue("Usuario o contraseña incorrectos")
                }

            } catch (e: IOException) {
                mensajeUi.postValue("No se ha podido conectar con el servidor")
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            mensajeUi.value = "Por favor, rellena todos los campos"
        }

        return idUsuario

    }

    fun registrarse(nombre: String, email: String, passwd: String, context: Context) : Int? {

        var idUsuario: Int? = null;

        if (!(nombre.isBlank() || nombre.isEmpty()) && !(email.isBlank() || email.isEmpty()) && !(passwd.isBlank() || passwd.isEmpty())) {
            var dirIp = "192.168.1.39"
            var puerto = 8484

            val conf = Properties()
            try {
                val inputStream = context.assets.open("client.properties")
                conf.load(inputStream)
                puerto = Integer.parseInt(conf.getProperty("PUERTO"))
                dirIp = conf.getProperty("IP")
                println("$puerto, $dirIp")
            } catch (e: IOException) {
                println("No se ha podido leer el archivo de propiedades")
            }

            try {

                val cliente = Socket(dirIp, puerto)
                updateSocket(cliente)
                val entrada = DataInputStream(cliente.getInputStream())
                val salida = DataOutputStream(cliente.getOutputStream())

                salida.writeUTF("registro;$email;$passwd;$nombre")
                val fromServer: List<String> = entrada.readUTF().split(";")

                if (fromServer[0] == "correcto") {
                    mensajeUi.postValue("Sesión iniciada correctamente")
                    idUsuario = fromServer[1].toInt()
                } else {
                    mensajeUi.postValue("Ese email ya está en uso")
                }

            } catch (e: IOException) {
                mensajeUi.postValue("No se ha podido conectar con el servidor")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            mensajeUi.value = "Por favor, rellena todos los campos"
        }

        return idUsuario

    }

}