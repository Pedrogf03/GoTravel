package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Etapa
import com.gotravel.mobile.data.model.Imagen
import com.gotravel.mobile.data.model.Resena
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.ServicioDestination
import com.gotravel.mobile.ui.screen.ViajeDestination
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.Sesion
import com.gotravel.mobile.ui.utils.formatoFinal
import com.gotravel.mobile.ui.utils.formatoFromDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate

sealed interface ServicioUiState {
    data class Success(val servicio: Servicio) : ServicioUiState
    object Error : ServicioUiState
    object Loading : ServicioUiState
}

class ServicioViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val idServicio: Int = checkNotNull(savedStateHandle[ServicioDestination.idServicio])

    var mensajeUi: MutableLiveData<String> = MutableLiveData()

    var uiState: ServicioUiState by mutableStateOf(ServicioUiState.Loading)
        private set

    init {
        getServicio()
    }

    private fun getServicio() {
        viewModelScope.launch {
            uiState = try {
                val servicio = findServicioById()
                if(servicio != null) {
                    ServicioUiState.Success(servicio)
                } else {
                    ServicioUiState.Error
                }
            } catch (e: IOException) {
                ServicioUiState.Error
            }
        }
    }
    private suspend fun findServicioById() : Servicio? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findById;servicio;${idServicio}")
                    Sesion.salida.flush()

                    val servicioFromServer = Sesion.entrada.readUTF()
                    val servicio =  gson.fromJson(servicioFromServer, Servicio::class.java)
                    println("servicio " + servicioFromServer)

                    if(servicio != null) {
                        servicio.imagenes = getAllImagenesFromServicio()!!
                    }

                    Sesion.salida.writeUTF("findUsuarioByServicio;${idServicio}")
                    Sesion.salida.flush()

                    val usuarioFromServer = Sesion.entrada.readUTF()
                    val usuario = gson.fromJson(usuarioFromServer, Usuario::class.java)

                    if(usuario != null) {
                        servicio.usuario = usuario
                    }

                    Sesion.salida.writeUTF("findResenasByServicio;${idServicio}")
                    Sesion.salida.flush()

                    val resenasFromServer = Sesion.entrada.readUTF()
                    val type = object : TypeToken<List<Resena>>() {}.type
                    val resenas =  gson.fromJson<List<Resena>>(resenasFromServer, type)

                    servicio.resenas = resenas

                    return@withContext servicio

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                    return@withContext null
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return@withContext null
            }
        } else {
            return null
        }

    }

    private suspend fun getAllImagenesFromServicio(): List<Imagen>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findImagesFromServicioId;${idServicio};all")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val type = object : TypeToken<List<Imagen>>() {}.type
                    val imagenes =  gson.fromJson<List<Imagen>>(jsonFromServer, type)

                    for(imagen in imagenes) {
                        val length = Sesion.entrada.readInt() // Lee la longitud del ByteArray
                        val byteArray = ByteArray(length)
                        Sesion.entrada.readFully(byteArray) // Lee el ByteArray
                        imagen.imagen = byteArray
                    }

                    //Sesion.entrada.readUTF()

                    return@withContext imagenes

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                    return@withContext null
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return@withContext null
            }
        } else {
            return null
        }

    }

    suspend fun subirFoto(context: Context, selectedImageUri: Uri?): Boolean {

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

                            Sesion.salida.writeUTF("uploadFoto;servicio;${idServicio}")
                            Sesion.salida.flush()

                            Sesion.salida.writeInt(byteArray.size) // Envía el tamaño del array de bytes
                            Sesion.salida.write(byteArray) // Envía el array de bytes
                            Sesion.salida.flush()

                            val gson = GsonBuilder()
                                .serializeNulls()
                                .setLenient()
                                .create()

                            val jsonFromServer = Sesion.entrada.readUTF()
                            val servicio = gson.fromJson(jsonFromServer, Servicio::class.java)

                            if (servicio != null) {
                                getServicio()
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

    suspend fun deleteFoto(imagen: Imagen, onImageDeleted: () -> Unit) {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("delete;imagen;${imagen.id}")
                    Sesion.salida.flush()

                    val confirm = Sesion.entrada.readBoolean()

                    if(confirm) {
                        onImageDeleted()
                        getServicio()
                    }

                    //Sesion.entrada.readUTF()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }


}