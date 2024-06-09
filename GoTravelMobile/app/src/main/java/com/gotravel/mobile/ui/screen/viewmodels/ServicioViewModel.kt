package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Imagen
import com.gotravel.mobile.data.model.Resena
import com.gotravel.mobile.data.model.ResenaId
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.screen.ServicioDestination
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

sealed interface ServicioUiState {
    data class Success(val servicio: Servicio) : ServicioUiState
    object Error : ServicioUiState
    object Loading : ServicioUiState
}

class ServicioViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val idServicio: Int = checkNotNull(savedStateHandle[ServicioDestination.idServicio])
    private val idEtapa: Int? = savedStateHandle[ServicioDestination.idEtapa]

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

                    if(servicio != null) {
                        servicio.imagenes = getAllImagenesFromServicio()!!
                        servicio.contratado = isServicioContratado()

                        Sesion.salida.writeUTF("findByServicioId;usuario;${idServicio}")
                        Sesion.salida.flush()

                        val usuarioFromServer = Sesion.entrada.readUTF()
                        val usuario = gson.fromJson(usuarioFromServer, Usuario::class.java)

                        if(usuario != null) {
                            servicio.usuario = usuario
                        }

                        Sesion.salida.writeUTF("findByServicioId;resenas;${idServicio}")
                        Sesion.salida.flush()

                        val resenasFromServer = Sesion.entrada.readUTF()
                        val type = object : TypeToken<List<Resena>>() {}.type
                        val resenas =  gson.fromJson<List<Resena>>(resenasFromServer, type)

                        for(r in resenas) {
                            val usuarioTieneFoto = Sesion.entrada.readBoolean()
                            if(usuarioTieneFoto) {
                                val length = Sesion.entrada.readInt() // Lee la longitud del ByteArray
                                val byteArray = ByteArray(length)
                                Sesion.entrada.readFully(byteArray) // Lee el ByteArray
                                r.usuario!!.foto = byteArray
                            }
                        }

                        servicio.resenas = resenas

                        return@withContext servicio
                    }

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

    private suspend fun isServicioContratado(): Boolean {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {

                try {

                    Sesion.salida.writeUTF("servicio;isContratado;${idServicio}")
                    Sesion.salida.flush()

                    return@withContext Sesion.entrada.readBoolean()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                    return@withContext false
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return@withContext false

            }
        }

        return false

    }

    private suspend fun getAllImagenesFromServicio(): List<Imagen>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findByServicioId;imagen;$idServicio;all")
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

                try {

                    Sesion.salida.writeUTF("delete;imagen;${imagen.id}")
                    Sesion.salida.flush()

                    val confirm = Sesion.entrada.readBoolean()

                    if(confirm) {
                        onImageDeleted()
                        getServicio()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    suspend fun publicarServicio() {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("servicio;publicar;${idServicio}")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF();
                    val servicioFromServer = gson.fromJson(jsonFromServer, Servicio::class.java)

                    if(servicioFromServer != null) {
                        getServicio()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    suspend fun archivarServicio() {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("servicio;archivar;${idServicio}")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF();
                    val servicioFromServer = gson.fromJson(jsonFromServer, Servicio::class.java)

                    if(servicioFromServer != null) {
                        getServicio()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    suspend fun addResena(nota: String, comentario: String) : Boolean{

        if(nota.isBlank() || comentario.isBlank()) {
            mensajeUi.postValue("Rellena todos los campos")
        } else {

            var puntuacion: Int = -1

            try {
                puntuacion = nota.toInt()
            } catch (e: NumberFormatException) {
                mensajeUi.postValue("La nota no es válida")
            }

            if(puntuacion != -1 && comentario.matches(Regex.regexCamposAlfaNum)) {

                val resenaId = ResenaId(idServicio = idServicio, idUsuario = Sesion.usuario.id!!)
                val resena = Resena(id = resenaId, puntuacion = puntuacion, contenido = comentario, oculto = "0")

                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                    return withContext(Dispatchers.IO) {

                        try {

                            Sesion.salida.writeUTF("save;resena")
                            Sesion.salida.flush()

                            Sesion.salida.writeUTF(gson.toJson(resena))
                            Sesion.salida.flush()

                            val jsonFromServer = Sesion.entrada.readUTF()
                            val resenaFromServer = gson.fromJson(jsonFromServer, Resena::class.java)

                            if(resenaFromServer != null) {
                                getServicio()
                                return@withContext true
                            }

                            return@withContext false

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Sesion.socket!!.close()
                            return@withContext false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return@withContext false

                    }
                }
            } else {
                mensajeUi.postValue("El comentario no es válido")
            }

        }

        return false

    }

    suspend fun contratarServicio(context: Context) {
        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            withContext(Dispatchers.IO) {

                try {

                    Sesion.salida.writeUTF("servicio;contratar;${idServicio};${idEtapa!!}")
                    Sesion.salida.flush()

                    val url = Sesion.entrada.readUTF()

                    if(url != null) {
                        // Crea un Intent para abrir el enlace de aprobación en el navegador
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }

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