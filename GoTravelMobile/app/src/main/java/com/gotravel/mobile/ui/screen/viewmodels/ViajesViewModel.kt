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
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.ViajesDestination
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.formatoFinal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

sealed interface ViajesUiState {
    data class Success(val viajesPasados: List<Viaje>, val viajes: List<Viaje>) : ViajesUiState
    object Error : ViajesUiState
    object Loading : ViajesUiState
}

@RequiresApi(Build.VERSION_CODES.O)
class ViajesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val busqueda: String? = savedStateHandle[ViajesDestination.busqueda]

    var uiState: ViajesUiState by mutableStateOf(ViajesUiState.Loading)
        private set

    init {
        getViajes()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getViajes() {
        viewModelScope.launch {
            try {
                val allViajes = findViajesByUsuarioId()
                val viajesPasados = mutableListOf<Viaje>()
                val viajes = mutableListOf<Viaje>()
                if(busqueda != null) {
                    for(viaje in allViajes) {
                        if(viaje.nombre.lowercase().contains(busqueda.lowercase())){
                            if(LocalDate.parse(viaje.final, formatoFinal).isBefore(LocalDate.now())) { // Si el final del viaje es antes del dia de hoy
                                viajesPasados.add(viaje)
                            } else {
                                viajes.add(viaje)
                            }
                        }
                    }
                } else {
                    for(viaje in allViajes) {
                        if(LocalDate.parse(viaje.final, formatoFinal).isBefore(LocalDate.now())) { // Si el final del viaje es antes del dia de hoy
                            viajesPasados.add(viaje)
                        } else {
                            viajes.add(viaje)
                        }
                    }
                }
                uiState = ViajesUiState.Success(viajesPasados = viajesPasados, viajes = viajes)
            } catch (e: IOException) {
                uiState = ViajesUiState.Error
            }
        }
    }

    private suspend fun findViajesByUsuarioId() : List<Viaje> {
        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {

                AppUiState.salida.writeUTF("findByUserId;viaje")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                val type = object : TypeToken<List<Viaje>>() {}.type
                return@withContext gson.fromJson<List<Viaje>?>(jsonFromServer, type)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf<Viaje>()
        }
    }

}