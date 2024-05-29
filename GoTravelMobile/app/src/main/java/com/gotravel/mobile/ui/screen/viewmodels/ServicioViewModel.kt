package com.gotravel.mobile.ui.screen.viewmodels

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
import com.gotravel.mobile.data.model.Servicio
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

                    val jsonFromServer = Sesion.entrada.readUTF()
                    println("json " + jsonFromServer)
                    val servicio =  gson.fromJson(jsonFromServer, Servicio::class.java)

                    if(servicio != null) {
                        servicio.imagenes = getAllImagenesFromServicio(servicio.id)!!
                    }

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

    private suspend fun getAllImagenesFromServicio(id: Int?): List<Imagen>? {

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

                    Sesion.entrada.readUTF()

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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun actualizarViaje(nombre: String, descripcion: String, fechaInicio: String, fechaFin: String, viaje: Viaje): Boolean {

        if(nombre.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos obligatorios")
        } else {

            val inicio = LocalDate.parse(fechaInicio, formatoFinal)
            val fin = LocalDate.parse(fechaFin, formatoFinal)

            if(!nombre.matches(Regex.regexCamposAlfaNum) || nombre.length >= 45) {
                mensajeUi.postValue("El nombre no es válido")
            } else if(descripcion.isNotBlank() && !descripcion.matches(Regex.regexCamposAlfaNum)) {
                mensajeUi.postValue("La descripción no es válida")
            } else if (fin.isBefore(inicio)) {
                mensajeUi.postValue("La fecha de final no puede ser antes que la fecha de inicio")
            } else {


                if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                    return withContext(Dispatchers.IO) {
                        val viajeActualizado = viaje.copy(nombre = nombre, descripcion = descripcion.ifBlank { null }, fechaInicio = inicio.format(formatoFromDb), fechaFin = fin.format(formatoFromDb))

                        val gson = GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create()

                        val viajeFromServer : Viaje?

                        try {

                            Sesion.salida.writeUTF("update;viaje")
                            Sesion.salida.flush()

                            val json = gson.toJson(viajeActualizado)
                            Sesion.salida.writeUTF(json)
                            Sesion.salida.flush()

                            val jsonFromServer = Sesion.entrada.readUTF()
                            viajeFromServer = gson.fromJson(jsonFromServer, Viaje::class.java)

                            return@withContext viajeFromServer != null

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

}