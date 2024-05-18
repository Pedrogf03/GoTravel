package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.sha256
import java.io.ByteArrayOutputStream
import java.io.IOException

class PerfilViewModel : ViewModel() {

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    fun updateUsuario(usuario: Usuario): Boolean {

        if(!(usuario.nombre.isBlank() || usuario.nombre.isEmpty()) && usuario.nombre.matches(Regex.regexNombre)) {

            if(usuario.apellidos != null && usuario.apellidos!!.matches(Regex.regexCamposGrandes) ) {

                if(!(usuario.email.isBlank() || usuario.email.isEmpty()) && usuario.email.matches(Regex.regexEmail)) {

                    if(usuario.tfno != null && usuario.tfno!!.matches(Regex.regexTfno) ) {

                        val gson = GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create()

                        val usuarioFromServer : Usuario?

                        try {

                            AppUiState.salida.writeUTF("update;${AppUiState.usuario.id};usuario")
                            AppUiState.salida.flush()

                            val json = gson.toJson(usuario)
                            AppUiState.salida.writeUTF(json)
                            AppUiState.salida.flush()

                            val jsonFromServer = AppUiState.entrada.readUTF()
                            usuarioFromServer = gson.fromJson(jsonFromServer, Usuario::class.java)

                            if (usuarioFromServer != null) {
                                AppUiState.usuario = usuarioFromServer.copy(foto = AppUiState.usuario.foto) // Al recibir el usuario se le vuelve a poner la foto que tenía
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

                    } else {
                        mensajeUi.postValue("El número de telefono no es válido")
                    }

                } else {
                    mensajeUi.postValue("El email no es válido")
                }

            } else {
                mensajeUi.postValue("Los apellidos no son válidos")
            }

        } else {
            mensajeUi.postValue("El nombre no es válido")
        }

        return false

    }

    private fun reduceImageQuality(byteArray: ByteArray, quality: Int): ByteArray {
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

                    AppUiState.salida.writeUTF("uploadFoto;${AppUiState.usuario.id};usuario")
                    AppUiState.salida.flush()

                    AppUiState.salida.writeInt(byteArray.size) // Envía el tamaño del array de bytes
                    AppUiState.salida.write(byteArray) // Envía el array de bytes
                    AppUiState.salida.flush()

                    val gson = GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create()

                    val jsonFromServer = AppUiState.entrada.readUTF()
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

    fun updateContrasena(contrasenaActual: String, contrasenaNueva: String, confirmarContrasena: String) : Boolean {

        if((!(contrasenaActual.isBlank() || contrasenaActual.isEmpty())) && (!(contrasenaNueva.isBlank() || contrasenaNueva.isEmpty())) && (!(confirmarContrasena.isBlank() || confirmarContrasena.isEmpty()))) {

            if(contrasenaNueva.matches(Regex.regexContrasena)) {

                val contrasenaNuevaHash = contrasenaNueva.sha256()
                val confirmarHash = confirmarContrasena.sha256()
                val contrasenaActualHash = contrasenaActual.sha256()

                if(contrasenaNuevaHash == contrasenaActualHash) {
                    mensajeUi.postValue("La nueva contraseña no puede ser igual a la anterior")
                } else if (contrasenaNuevaHash != confirmarHash) {
                    mensajeUi.postValue("Las contraseñas no coinciden")
                } else {

                    val gson = GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create()

                    val usuarioFromServer : Usuario?

                    try {

                        AppUiState.salida.writeUTF("updateContrasena;${AppUiState.usuario.id};${contrasenaActualHash};${contrasenaNuevaHash}")
                        AppUiState.salida.flush()

                        val jsonFromServer = AppUiState.entrada.readUTF()
                        usuarioFromServer = gson.fromJson(jsonFromServer, Usuario::class.java)

                        if (usuarioFromServer != null) {
                            AppUiState.usuario = usuarioFromServer.copy(foto = AppUiState.usuario.foto)
                            return true
                        } else {
                            mensajeUi.postValue("Contraseña incorrecta")
                            return false
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            } else {
                mensajeUi.postValue("La nueva contraseña no es válida")
            }

        } else {
            mensajeUi.postValue("Por favor, rellena todos los campos")
        }

        return false

    }

}