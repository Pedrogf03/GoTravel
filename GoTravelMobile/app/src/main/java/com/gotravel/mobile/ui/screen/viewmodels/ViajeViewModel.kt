package com.gotravel.mobile.ui.screen.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Etapa
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.ViajeDestination
import com.gotravel.mobile.ui.utils.AppUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface ViajeUiState {
    data class Success(val viaje: Viaje, val etapas: List<Etapa>) : ViajeUiState
    object Error : ViajeUiState
    object Loading : ViajeUiState
}

class ViajeViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val idViaje: Int = checkNotNull(savedStateHandle[ViajeDestination.idViaje])

    var uiState: ViajeUiState by mutableStateOf(ViajeUiState.Loading)
        private set

    init {
        getAllFromViaje()
    }

    fun getAllFromViaje() {
        viewModelScope.launch {
            try {
                val viaje = findViajeById(idViaje)
                if(viaje != null) {
                    val etapasFromViaje = findEtapasFromViajeId(viaje.id!!)
                    uiState = ViajeUiState.Success(viaje, etapasFromViaje)
                } else {
                    uiState = ViajeUiState.Error
                }
            } catch (e: IOException) {
                uiState = ViajeUiState.Error
            }
        }
    }

    private suspend fun findViajeById(idViaje: Int) : Viaje? {

        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {

                AppUiState.salida.writeUTF("findViajeById;${AppUiState.usuario.id};${idViaje}")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                return@withContext gson.fromJson(jsonFromServer, Viaje::class.java)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext null
        }

    }

    private suspend fun findEtapasFromViajeId(idViaje: Int) : List<Etapa> {

        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {

                AppUiState.salida.writeUTF("findEtapasFromViajeId;${AppUiState.usuario.id};${idViaje}")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                val type = object : TypeToken<List<Etapa>>() {}.type
                return@withContext gson.fromJson(jsonFromServer, type)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf()
        }

    }

}