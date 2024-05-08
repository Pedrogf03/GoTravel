package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Usuario
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import kotlin.math.min

class PerfilViewModel : ViewModel() {

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    private fun updateUsuario(usuario: Usuario): Boolean {

        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        val usuarioFromServer : Usuario?

        try {

            val salida = DataOutputStream(AppUiState.socket.getOutputStream())
            val entrada = DataInputStream(AppUiState.socket.getInputStream())

            salida.writeUTF("update;${AppUiState.usuario.id};usuario")
            salida.flush()

            val json = gson.toJson(usuario)
            salida.writeUTF(json)
            salida.flush()

            val jsonFromServer = entrada.readUTF()
            usuarioFromServer = gson.fromJson(jsonFromServer, Usuario::class.java)

            if (usuarioFromServer != null) {
                AppUiState.usuario = usuarioFromServer.copy()
                return true
            } else {
                return false
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false

    }

    fun updateNombre(nombre: String) {

        if(!(nombre.isBlank() || nombre.isEmpty()) && nombre.matches(Regex.regexNombre)) {

            val usuario = AppUiState.usuario.copy()
            usuario.nombre = nombre
            if(updateUsuario(usuario)) {
                mensajeUi.postValue("Nombre cambiado con éxito")
            } else {
                mensajeUi.postValue("No se ha podido cambiar el nombre")
            }

        } else {
            mensajeUi.postValue("El nombre no es válido")
        }

    }

    fun updateApellidos(apellidos: String) {

        if((apellidos.isBlank() || apellidos.isEmpty())) {

            val usuario = AppUiState.usuario.copy()
            usuario.apellidos = null
            if(updateUsuario(usuario)) {
                mensajeUi.postValue("Apellidos cambiados con éxito")
            } else {
                mensajeUi.postValue("No se ha podido cambiar los apellidos")
            }

        } else if (apellidos.matches(Regex.regexNombre)) {

            val usuario = AppUiState.usuario.copy()
            usuario.apellidos = apellidos
            if(updateUsuario(usuario)) {
                mensajeUi.postValue("Apellidos cambiados con éxito")
            } else {
                mensajeUi.postValue("No se ha podido cambiar los apellidos")
            }

        } else {
            mensajeUi.postValue("Los apellidos no son válidos")
        }

    }

    fun updateEmail(email: String) {

        if(!(email.isBlank() || email.isEmpty()) && email.matches(Regex.regexEmail)) {

            val usuario = AppUiState.usuario.copy()
            usuario.email = email
            if(updateUsuario(usuario)) {
                mensajeUi.postValue("Email cambiado con éxito")
            } else {
                mensajeUi.postValue("No se ha podido cambiar el email")
            }

        } else {
            mensajeUi.postValue("El email no es válido")
        }

    }

    fun updateTfno(tfno: String) {

        if((tfno.isBlank() || tfno.isEmpty())) {

            val usuario = AppUiState.usuario.copy()
            usuario.tfno = null
            if(updateUsuario(usuario)) {
                mensajeUi.postValue("Teléfono cambiado con éxito")
            } else {
                mensajeUi.postValue("No se ha podido cambiar el teléfono")
            }

        } else if (tfno.matches(Regex.regexTfno)) {

            val usuario = AppUiState.usuario.copy()
            usuario.tfno = tfno
            if(updateUsuario(usuario)) {
                mensajeUi.postValue("Teléfono cambiado con éxito")
            } else {
                mensajeUi.postValue("No se ha podido cambiar el teléfono")
            }

        } else {
            mensajeUi.postValue("El teléfono no es válido")
        }

    }

    fun reduceImageQuality(byteArray: ByteArray, quality: Int): ByteArray {
        // Decodifica el ByteArray a un Bitmap
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        // Crea un nuevo ByteArrayOutputStream
        val outputStream = ByteArrayOutputStream()

        // Comprime el Bitmap en el ByteArrayOutputStream
        // Utiliza un valor de calidad menor para reducir el tamaño del ByteArray resultante
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Convierte el ByteArrayOutputStream a un ByteArray
        return outputStream.toByteArray()
    }

    fun updateFoto(context: Context, selectedImageUri: Uri?) : Boolean {

        selectedImageUri?.let { uri ->
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                val byteArray = reduceImageQuality(byteArrayOutputStream.toByteArray(), 30)

                try {

                    val salida = DataOutputStream(AppUiState.socket.getOutputStream())
                    val entrada = DataInputStream(AppUiState.socket.getInputStream())

                    salida.writeUTF("uploadFoto;${AppUiState.usuario.id};usuario")
                    salida.flush()

                    salida.writeInt(byteArray.size) // Envía el tamaño del array de bytes
                    salida.write(byteArray) // Envía el array de bytes
                    salida.flush()

                    val gson = GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create()

                    val jsonFromServer = entrada.readUTF()
                    val usuario = gson.fromJson(jsonFromServer, Usuario::class.java)

                    if (usuario != null) {
                        AppUiState.usuario.foto = byteArray
                        return true
                    } else {
                        mensajeUi.postValue("Lo sentimos, la foto que has seleccionado es demasiado grande")
                        return false
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        return false

    }


}
