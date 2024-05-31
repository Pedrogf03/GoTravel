package com.gotravel.mobile.ui.screen.viewmodels

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


sealed interface ContratacionesUiState {
    data class Success(val servicios: List<Servicio>) : ContratacionesUiState
    object Error : ContratacionesUiState
    object Loading : ContratacionesUiState
}

class ContratacionesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val busqueda: String? = savedStateHandle[ViajesDestination.busqueda]

    var uiState: ContratacionesUiState by mutableStateOf(ContratacionesUiState.Loading)
        private set

    init {
        getServicios()
    }

    private fun getServicios() {

        viewModelScope.launch {
            uiState = try {
                val allServicios = findAllServiciosContratados()
                if(allServicios != null){
                    val servicios = mutableListOf<Servicio>()
                    if(busqueda != null) {
                         for(s in allServicios) {
                             if(s.nombre.contains(busqueda)){
                                 servicios.add(s)
                             }
                         }
                    } else {
                        servicios.addAll(allServicios)
                    }
                    ContratacionesUiState.Success(servicios)
                } else {
                    ContratacionesUiState.Error
                }
            } catch (e: IOException) {
                ContratacionesUiState.Error
            }
        }

    }

    private suspend fun findAllServiciosContratados(): List<Servicio>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findAllServiciosContratados")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val type = object : TypeToken<List<Servicio>>() {}.type
                    val servicios = gson.fromJson<List<Servicio>>(jsonFromServer, type)

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

                return@withContext null
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

                    Sesion.salida.writeUTF("findImagesFromServicioId;$id;one")
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