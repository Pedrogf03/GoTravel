package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.utils.Sesion
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.sha256
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

class PerfilViewModel : ViewModel() {

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    suspend fun updateUsuario(usuario: Usuario): Boolean {

        if(!(usuario.nombre.isBlank() || usuario.nombre.isEmpty()) && usuario.nombre.matches(Regex.regexCamposAlfaNum) && usuario.nombre.length <= 45) {

            if(usuario.apellidos != null && usuario.apellidos!!.matches(Regex.regexCamposAlfaNum) && usuario.apellidos!!.length <= 200 ) {

                if(!(usuario.email.isBlank() || usuario.email.isEmpty()) && usuario.email.matches(Regex.regexEmail)) {

                    if(usuario.tfno != null && usuario.tfno!!.matches(Regex.regexTfno) ) {

                        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                            return withContext(Dispatchers.IO) {
                                val gson = GsonBuilder()
                                    .serializeNulls()
                                    .setLenient()
                                    .create()

                                val usuarioFromServer : Usuario?

                                try {

                                    Sesion.salida.writeUTF("update;usuario")
                                    Sesion.salida.flush()

                                    val json = gson.toJson(usuario)
                                    Sesion.salida.writeUTF(json)
                                    Sesion.salida.flush()

                                    val jsonFromServer = Sesion.entrada.readUTF()
                                    usuarioFromServer = gson.fromJson(jsonFromServer, Usuario::class.java)

                                    if (usuarioFromServer != null) {
                                        Sesion.usuario = usuarioFromServer.copy(foto = Sesion.usuario.foto) // Al recibir el usuario se le vuelve a poner la foto que tenía
                                        return@withContext true
                                    } else {
                                        return@withContext false
                                    }

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    Sesion.socket!!.close()
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

                if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                    return withContext(Dispatchers.IO) {
                        try {

                            Sesion.salida.writeUTF("uploadFoto;usuario")
                            Sesion.salida.flush()

                            Sesion.salida.writeInt(byteArray.size) // Envía el tamaño del array de bytes
                            Sesion.salida.write(byteArray) // Envía el array de bytes
                            Sesion.salida.flush()

                            val gson = GsonBuilder()
                                .serializeNulls()
                                .setLenient()
                                .create()

                            val jsonFromServer = Sesion.entrada.readUTF()
                            val usuario = gson.fromJson(jsonFromServer, Usuario::class.java)

                            if (usuario != null) {
                                Sesion.usuario.foto = byteArray
                                return@withContext true
                            } else {
                                mensajeUi.postValue("Lo sentimos, la foto que has seleccionado es demasiado grande")
                                return@withContext false
                            }

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Sesion.socket!!.close()
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

                    if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                        return withContext(Dispatchers.IO) {
                            val gson = GsonBuilder()
                                .serializeNulls()
                                .setLenient()
                                .create()

                            val usuarioFromServer : Usuario?

                            try {

                                Sesion.salida.writeUTF("updateContrasena;${contrasenaActualHash};${contrasenaNuevaHash}")
                                Sesion.salida.flush()

                                val jsonFromServer = Sesion.entrada.readUTF()
                                usuarioFromServer = gson.fromJson(jsonFromServer, Usuario::class.java)

                                if (usuarioFromServer != null) {
                                    Sesion.usuario = usuarioFromServer.copy(foto = Sesion.usuario.foto)
                                    return@withContext true
                                } else {
                                    mensajeUi.postValue("Contraseña incorrecta")
                                    return@withContext false
                                }

                            } catch (e: IOException) {
                                e.printStackTrace()
                                Sesion.socket!!.close()
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

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            try{
                Sesion.salida.writeUTF("cerrarSesion")
                Sesion.salida.flush()

                Sesion.salida.close()
                Sesion.entrada.close()
                Sesion.socket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Sesion.socket!!.close()
            }
        }

    }

    fun cerrarServidor() {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            try{
                Sesion.salida.writeUTF("cerrarServidor")
                Sesion.salida.flush()

                Sesion.salida.close()
                Sesion.entrada.close()
                Sesion.socket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Sesion.socket!!.close()
            }
        }

    }

}