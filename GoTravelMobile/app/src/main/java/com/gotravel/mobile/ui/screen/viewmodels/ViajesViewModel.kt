package com.gotravel.mobile.ui.screen.viewmodels

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface ViajesUiState {
    data class Success(val viajes: List<Viaje>) : ViajesUiState
    object Error : ViajesUiState
    object Loading : ViajesUiState
}

class ViajesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val busqueda: String? = savedStateHandle[ViajesDestination.busqueda]

    var uiState: ViajesUiState by mutableStateOf(ViajesUiState.Loading)
        private set

    init {
        getViajes()
    }

    fun getViajes() {
        viewModelScope.launch {
            try {
                val viajes = mutableListOf<Viaje>()
                val allViajes = findViajesByUsuarioId()
                if(busqueda != null) {
                    for(viaje in allViajes) {
                        if(viaje.nombre.lowercase().contains(busqueda.lowercase())){
                            viajes.add(viaje)
                        }
                    }
                } else {
                    viajes.addAll(allViajes)
                }
                uiState = ViajesUiState.Success(viajes)
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

                AppUiState.salida.writeUTF("consultarViajes;${AppUiState.usuario.id}")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                val type = object : TypeToken<List<Viaje>>() {}.type
                val viajes : List<Viaje>  = gson.fromJson(jsonFromServer, type)
                return@withContext viajes

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf<Viaje>()
        }
    }

}