package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.formatoFromDb
import com.gotravel.mobile.ui.utils.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

class PerfilViewModel : ViewModel() {

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    suspend fun updateUsuario(usuario: Usuario): Boolean {

        if(!(usuario.nombre.isBlank() || usuario.nombre.isEmpty()) && usuario.nombre.matches(Regex.regexNombre)) {

            if(usuario.apellidos != null && usuario.apellidos!!.matches(Regex.regexCamposGrandes) ) {

                if(!(usuario.email.isBlank() || usuario.email.isEmpty()) && usuario.email.matches(Regex.regexEmail)) {

                    if(usuario.tfno != null && usuario.tfno!!.matches(Regex.regexTfno) ) {

                        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
                            return withContext(Dispatchers.IO) {
                                val gson = GsonBuilder()
                                    .serializeNulls()
                                    .setLenient()
                                    .create()

                                val usuarioFromServer : Usuario?

                                try {

                                    AppUiState.salida.writeUTF("update;usuario")
                                    AppUiState.salida.flush()

                                    val json = gson.toJson(usuario)
                                    AppUiState.salida.writeUTF(json)
                                    AppUiState.salida.flush()

                                    val jsonFromServer = AppUiState.entrada.readUTF()
                                    usuarioFromServer = gson.fromJson(jsonFromServer, Usuario::class.java)

                                    if (usuarioFromServer != null) {
                                        AppUiState.usuario = usuarioFromServer.copy(foto = AppUiState.usuario.foto) // Al recibir el usuario se le vuelve a poner la foto que tenía
                                        return@withContext true
                                    } else {
                                        return@withContext false
                                    }

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    AppUiState.socket!!.close()
                                    mensajeUi.postValue("No se puede conectar con el servidor")
                                    return@withContext false
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                return@withContext false
                            }
                        } else {
                            return false
                        }

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

    suspend fun updateFoto(context: Context, selectedImageUri: Uri?) : Boolean {

        selectedImageUri?.let { uri ->
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                val byteArray = byteArrayOutputStream.toByteArray()

                if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
                    return withContext(Dispatchers.IO) {
                        try {

                            AppUiState.salida.writeUTF("uploadFoto;usuario")
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
                                return@withContext true
                            } else {
                                mensajeUi.postValue("Lo sentimos, la foto que has seleccionado es demasiado grande")
                                return@withContext false
                            }

                        } catch (e: IOException) {
                            e.printStackTrace()
                            AppUiState.socket!!.close()
                            mensajeUi.postValue("No se puede conectar con el servidor")
                            return@withContext false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return@withContext false
                    }
                } else {
                    return false
                }

            }
        }

        return false

    }

    suspend fun updateContrasena(contrasenaActual: String, contrasenaNueva: String, confirmarContrasena: String) : Boolean {

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

                    if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
                        return withContext(Dispatchers.IO) {
                            val gson = GsonBuilder()
                                .serializeNulls()
                                .setLenient()
                                .create()

                            val usuarioFromServer : Usuario?

                            try {

                                AppUiState.salida.writeUTF("updateContrasena;${contrasenaActualHash};${contrasenaNuevaHash}")
                                AppUiState.salida.flush()

                                val jsonFromServer = AppUiState.entrada.readUTF()
                                usuarioFromServer = gson.fromJson(jsonFromServer, Usuario::class.java)

                                if (usuarioFromServer != null) {
                                    AppUiState.usuario = usuarioFromServer.copy(foto = AppUiState.usuario.foto)
                                    return@withContext true
                                } else {
                                    mensajeUi.postValue("Contraseña incorrecta")
                                    return@withContext false
                                }

                            } catch (e: IOException) {
                                e.printStackTrace()
                                AppUiState.socket!!.close()
                                mensajeUi.postValue("No se puede conectar con el servidor")
                                return@withContext false
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            return@withContext false
                        }
                    } else {
                        return false
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

    fun cerrarSesion() {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            try{
                AppUiState.salida.writeUTF("cerrarSesion")
                AppUiState.salida.flush()

                AppUiState.salida.close()
                AppUiState.entrada.close()
                AppUiState.socket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
                AppUiState.socket!!.close()
            }
        }

    }

    fun cerrarServidor() {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            try{
                AppUiState.salida.writeUTF("cerrarServidor")
                AppUiState.salida.flush()

                AppUiState.salida.close()
                AppUiState.entrada.close()
                AppUiState.socket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
                AppUiState.socket!!.close()
            }
        }

    }

}