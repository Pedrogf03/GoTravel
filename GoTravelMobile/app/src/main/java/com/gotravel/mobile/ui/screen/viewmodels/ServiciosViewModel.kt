package com.gotravel.mobile.ui.screen.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Imagen
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.ui.screen.ViajesDestination
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface ServiciosUiState {
    data class Success(val serviciosOcultos: List<Servicio>, val serviciosPublicados: List<Servicio>) : ServiciosUiState
    object Error : ServiciosUiState
    object Loading : ServiciosUiState
}

@RequiresApi(Build.VERSION_CODES.O)
class ServiciosViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val busqueda: String? = savedStateHandle[ViajesDestination.busqueda]

    var uiState: ServiciosUiState by mutableStateOf(ServiciosUiState.Loading)
        private set

    init {
        getServicios()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getServicios() {
        viewModelScope.launch {
            try {
                val allServicios = findServiciosByUsuarioId()
                if (allServicios != null) {
                    val serviciosOcultos = mutableListOf<Servicio>()
                    val servicios = mutableListOf<Servicio>()
                    if(busqueda != null) {
                        for(servicio in allServicios) {
                            if(servicio.nombre.contains(busqueda) && servicio.publicado == "0") {
                                serviciosOcultos.add(servicio)
                            } else if(servicio.nombre.contains(busqueda) && servicio.publicado == "1") {
                                servicios.add(servicio)
                            }
                        }
                    } else {
                        for(servicio in allServicios) {
                            if(servicio.publicado == "0") {
                                serviciosOcultos.add(servicio)
                            } else if(servicio.publicado == "1") {
                                servicios.add(servicio)
                            }
                        }
                    }
                    uiState = ServiciosUiState.Success(serviciosOcultos = serviciosOcultos, serviciosPublicados = servicios)
                } else {
                    uiState = ServiciosUiState.Error
                }
            } catch (e: IOException) {
                uiState = ServiciosUiState.Error
            }
        }
    }

    private suspend fun findServiciosByUsuarioId() : List<Servicio>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findByUserId;servicio")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val type = object : TypeToken<List<Servicio>>() {}.type
                    val servicios =  gson.fromJson<List<Servicio>>(jsonFromServer, type)

                    for(s in servicios){
                        val firstImage = getFirstImagenFromServicio(s.id!!)
                        s.imagenes = listOfNotNull(firstImage)
                    }

                    return@withContext servicios

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                    return@withContext null
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return@withContext listOf<Servicio>()
            }
        } else {
            return null
        }

    }

    private suspend fun getFirstImagenFromServicio(id: Int): Imagen? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findByServicioId;imagen;$id;one")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val imagen = gson.fromJson(jsonFromServer, Imagen::class.java)

                    if(imagen != null) {
                        val length = Sesion.entrada.readInt() // Lee la longitud del ByteArray
                        val byteArray = ByteArray(length)
                        Sesion.entrada.readFully(byteArray) // Lee el ByteArray
                        imagen.imagen = byteArray
                    }

                    return@withContext imagen

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

}