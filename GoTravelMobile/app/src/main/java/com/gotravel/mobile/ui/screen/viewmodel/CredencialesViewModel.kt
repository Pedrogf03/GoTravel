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
import java.net.Socket
import java.util.Properties

class CredencialesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val opcion: String = checkNotNull(savedStateHandle[CredencialesDestination.opcion])

    private val _uiState = MutableStateFlow(CredencialesUiState())
    val uiState: StateFlow<CredencialesUiState> = _uiState.asStateFlow()

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    private fun updateSocket(cliente: Socket) {
        _uiState.update { currentState ->
            currentState.copy(socket = cliente)
        }
    }

    fun iniciarSesion(email: String, contrasena: String, context: Context) : Usuario? {

        mensajeUi.postValue("Conectando con el servidor...")
        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        var usuario : Usuario? = null

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

                val salida = DataOutputStream(cliente.getOutputStream())
                val entrada = DataInputStream(cliente.getInputStream())

                salida.writeUTF("login;$email;$contrasena")
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

        } else {
            mensajeUi.postValue("Por favor, rellena todos los campos")
        }

        return usuario

    }

    fun registrarse(nombre: String, email: String, contrasena: String, context: Context) : Usuario? {

        mensajeUi.postValue("Conectando con el servidor...")
        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        var usuario : Usuario? = null

        if (!(nombre.isBlank() || nombre.isEmpty()) && !(email.isBlank() || email.isEmpty()) && !(contrasena.isBlank() || contrasena.isEmpty())) {
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

                val salida = DataOutputStream(cliente.getOutputStream())
                val entrada = DataInputStream(cliente.getInputStream())

                salida.writeUTF("registro;$email;$contrasena;$nombre")
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

        } else {
            mensajeUi.postValue("Por favor, rellena todos los campos")
        }

        return usuario

    }

}

data class CredencialesUiState(
    val socket: Socket? = null
)