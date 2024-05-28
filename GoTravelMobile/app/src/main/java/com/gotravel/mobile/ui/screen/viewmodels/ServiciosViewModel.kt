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
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.data.model.Suscripcion
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.ViajesDestination
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.formatoFinal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

sealed interface ServiciosUiState {
    data class Success(val serviciosPasados: List<Servicio>, val servicios: List<Servicio>) : ServiciosUiState
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
                    val serviciosPasados = mutableListOf<Servicio>()
                    val servicios = mutableListOf<Servicio>()
                    if(busqueda != null) {
                        for(servicio in allServicios) {
                            if(servicio.fechaFinal != null) {
                                if(servicio.nombre.lowercase().contains(busqueda.lowercase())){
                                    if(LocalDate.parse(servicio.final, formatoFinal).isBefore(LocalDate.now())) { // Si el final del servicio es antes del dia de hoy
                                        serviciosPasados.add(servicio)
                                    } else {
                                        servicios.add(servicio)
                                    }
                                }
                            } else {
                                if(servicio.nombre.lowercase().contains(busqueda.lowercase())){
                                    if(LocalDate.parse(servicio.inicio, formatoFinal).isBefore(LocalDate.now())) { // Si no tiene fecha de fin y el inicio del servicio es antes del dia de hoy
                                        serviciosPasados.add(servicio)
                                    } else {
                                        servicios.add(servicio)
                                    }
                                }
                            }
                        }
                    } else {
                        for(servicio in allServicios) {
                            if(servicio.fechaFinal != null) {
                                if(LocalDate.parse(servicio.final, formatoFinal).isBefore(LocalDate.now())) { // Si el final del servicio es antes del dia de hoy
                                    serviciosPasados.add(servicio)
                                } else {
                                    servicios.add(servicio)
                                }
                            } else {
                                if(LocalDate.parse(servicio.inicio, formatoFinal).isBefore(LocalDate.now())) { // Si no tiene fecha de fin y el inicio del servicio es antes del dia de hoy
                                    serviciosPasados.add(servicio)
                                } else {
                                    servicios.add(servicio)
                                }
                            }
                        }
                    }
                    uiState = ServiciosUiState.Success(serviciosPasados = serviciosPasados, servicios = servicios)
                } else {
                    uiState = ServiciosUiState.Error
                }
            } catch (e: IOException) {
                uiState = ServiciosUiState.Error
            }
        }
    }

    private suspend fun findServiciosByUsuarioId() : List<Servicio>? {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    AppUiState.salida.writeUTF("findByUserId;servicio")
                    AppUiState.salida.flush()

                    val jsonFromServer = AppUiState.entrada.readUTF()
                    val type = object : TypeToken<List<Servicio>>() {}.type
                    return@withContext gson.fromJson<List<Servicio>?>(jsonFromServer, type)

                } catch (e: IOException) {
                    e.printStackTrace()
                    AppUiState.socket!!.close()
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

}