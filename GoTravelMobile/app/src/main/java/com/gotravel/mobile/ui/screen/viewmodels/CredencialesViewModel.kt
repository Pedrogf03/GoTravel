package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.gotravel.mobile.ClosingService
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.screen.CredencialesDestination
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.Sesion
import com.gotravel.mobile.ui.utils.sha256
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Properties

class CredencialesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val opcion: String = checkNotNull(savedStateHandle[CredencialesDestination.opcion])

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    fun conectarConServidor(vararg credenciales: String, context: Context) : Boolean {

        mensajeUi.postValue("Conectando con el servidor...")

        var peticion = ""

        if(credenciales.size == 2) {
            val email = credenciales[0]
            val contrasena = credenciales[1]
            peticion = iniciarSesion(email, contrasena) // Mensaje que le hace la peticion al servidor para iniciar sesion dado email y contraseña
        } else if (credenciales.size == 4) {
            val email = credenciales[0]
            val contrasena = credenciales[1]
            val nombre = credenciales[2]
            val confirmarContrasena = credenciales[3]
            peticion = registrarse(email, contrasena, nombre, confirmarContrasena)
        }

        if(peticion != "") {

            var dirIp = "localhost"
            var puerto = 8484

            val conf = Properties()
            try {
                val inputStream = context.openFileInput("client.properties")
                conf.load(inputStream)
                dirIp = conf.getProperty("IP")
                puerto = Integer.parseInt(conf.getProperty("PUERTO"))
            } catch (e: IOException) {
                println("No se ha podido leer el archivo de propiedades")
            }

            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            val usuario : Usuario

            try {
                val socketAddress = InetSocketAddress(dirIp, puerto)
                val cliente = Socket()
                val tiempoDeEspera = 1000 // Tiempo de espera en milisegundos

                cliente.connect(socketAddress, tiempoDeEspera)
                Sesion.socket = cliente

                Sesion.salida = DataOutputStream(Sesion.socket!!.getOutputStream())
                Sesion.entrada = DataInputStream(Sesion.socket!!.getInputStream())

                // Una vez iniciado el socket y flujos, se lanza el servicio de cerrar la aplicacion
                val intent = Intent(context, ClosingService::class.java)
                context.startService(intent)

                Sesion.salida.writeUTF(peticion) // El mensaje envíado dependerá de si se ha solicitado un inicio de sesión o un registro
                Sesion.salida.flush()

                val fromServer = Sesion.entrada.readUTF()
                if(fromServer.equals("reintentar")) {
                    if(peticion.contains("login")) {
                        mensajeUi.postValue("Email o contraseña incorrectos")
                    } else if (peticion.contains("registro")) {
                        mensajeUi.postValue("Ese email ya está en uso")
                    }

                    return false
                } else if(fromServer.equals("oculto")) {
                    if(peticion.contains("login")) {
                        mensajeUi.postValue("Ese usuario está oculto y no puede iniciar sesión")
                    }
                    return false
                } else {
                    usuario = gson.fromJson(fromServer, Usuario::class.java)

                    mensajeUi.postValue("Sesión iniciada correctamente")
                    Sesion.usuario = usuario

                    // Si se recibe un true (es decir, el usuario tiene foto asociada en la bbdd)
                    if(Sesion.entrada.readBoolean()) {
                        val length = Sesion.entrada.readInt() // Lee la longitud del ByteArray
                        val byteArray = ByteArray(length)
                        Sesion.entrada.readFully(byteArray) // Lee el ByteArray
                        Sesion.usuario.foto = byteArray
                    }

                    return true
                }

            } catch (e: IOException) {
                mensajeUi.postValue("No se ha podido conectar con el servidor")
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        return false

    }

    private fun iniciarSesion(email: String, contrasena: String) : String {

        if (!(email.isBlank() || email.isEmpty()) && !(contrasena.isBlank() || contrasena.isEmpty())) {

            return "login;${email.trim()};${contrasena.trim().sha256()}"

        } else {
            mensajeUi.postValue("Por favor, rellena todos los campos")
        }

        return ""

    }

    private fun registrarse(email: String, contrasena: String, nombre: String,  confirmarContrasena: String) : String {

        if (!(nombre.isBlank() || nombre.isEmpty()) && !(email.isBlank() || email.isEmpty()) && !(contrasena.isBlank() || contrasena.isEmpty()) && !(confirmarContrasena.isBlank() || confirmarContrasena.isEmpty())) {

            if (!nombre.matches(Regex.regexCamposAlfaNum) && nombre.length <= 45) {
                mensajeUi.postValue("El nombre no es válido")
                mensajeUi.postValue(nombre)
            } else if (!email.matches(Regex.regexEmail)) {
                mensajeUi.postValue("El email no es válido")
            } else if (!contrasena.matches(Regex.regexContrasena)) {
                mensajeUi.postValue("La contraseña no es válida")
            } else {

                val contrasenaHash = contrasena.trim().sha256()
                val confirmarHash = confirmarContrasena.trim().sha256()

                if(contrasenaHash != confirmarHash) {
                    mensajeUi.postValue("Las contraseñas no coinciden")
                } else {

                    return "registro;${email.trim()};$contrasenaHash;${nombre.trim()}"

                }

            }

        } else {
            mensajeUi.postValue("Por favor, rellena todos los campos")
        }

        return ""

    }

    fun recuperarContrasena(email: String, context: Context) : Boolean {

        var dirIp = "localhost"
        var puerto = 8484

        val conf = Properties()
        try {
            val inputStream = context.openFileInput("client.properties")
            conf.load(inputStream)
            dirIp = conf.getProperty("IP")
            puerto = Integer.parseInt(conf.getProperty("PUERTO"))
        } catch (e: IOException) {
            println("No se ha podido leer el archivo de propiedades")
        }


        try {
            val socketAddress = InetSocketAddress(dirIp, puerto)
            val cliente = Socket()
            val tiempoDeEspera = 1000 // Tiempo de espera en milisegundos

            cliente.connect(socketAddress, tiempoDeEspera)
            Sesion.socket = cliente

            Sesion.salida = DataOutputStream(Sesion.socket!!.getOutputStream())
            Sesion.entrada = DataInputStream(Sesion.socket!!.getInputStream())

            Sesion.salida.writeUTF("recuperarContrasena;${email}")
            Sesion.salida.flush()

            return Sesion.entrada.readBoolean()

        } catch (e: IOException) {
            mensajeUi.postValue("No se ha podido conectar con el servidor")
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false

    }

}

/*

 */