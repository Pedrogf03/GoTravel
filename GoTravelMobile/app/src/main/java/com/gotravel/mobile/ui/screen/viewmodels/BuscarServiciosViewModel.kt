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
import com.gotravel.mobile.ui.screen.BuscarServiciosDestination
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


sealed interface BuscarServiciosUiState {
    data class Success(val servicios: List<Servicio>) : BuscarServiciosUiState
    object Error : BuscarServiciosUiState
    object Loading : BuscarServiciosUiState
}
class BuscarServiciosViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val idEtapa: Int = checkNotNull(savedStateHandle[BuscarServiciosDestination.idEtapa])

    var uiState: BuscarServiciosUiState by mutableStateOf(BuscarServiciosUiState.Loading)
        private set

    init {
        getServicios()
    }

    private fun getServicios() {

        viewModelScope.launch {
            uiState = try {
                val servicios = findAllServiciosByFechasAndTipo()
                if(servicios != null) {
                    BuscarServiciosUiState.Success(servicios)
                } else {
                    BuscarServiciosUiState.Error
                }
            } catch (e: IOException) {
                BuscarServiciosUiState.Error
            }
        }

    }

    private suspend fun findAllServiciosByFechasAndTipo(): List<Servicio>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("servicio;${idEtapa};findAllByEtapa")
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