package com.gotravel.mobile.ui.screen.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Viaje
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

sealed interface ViajesUiState {
    data class Success(val viajes: List<Viaje>) : LandingUiState
    object Error : LandingUiState
    object Loading : LandingUiState
}

class ViajesViewModel : ViewModel() {

    var uiState: LandingUiState by mutableStateOf(ViajesUiState.Loading)
        private set

    init {
        getViajes()
    }

    private fun getViajes() {
        viewModelScope.launch {
            uiState = try {
                ViajesUiState.Success(findViajesByUsuarioId())
            } catch (e: IOException) {
                ViajesUiState.Error
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
                val salida = DataOutputStream(AppUiState.socket.getOutputStream())
                val entrada = DataInputStream(AppUiState.socket.getInputStream())

                salida.writeUTF("viajes;${AppUiState.usuario.id}")
                salida.flush()

                val jsonFromServer = entrada.readUTF()
                val type = object : TypeToken<List<Viaje>>() {}.type
                val viajes : List<Viaje>  = gson.fromJson(jsonFromServer, type)
                if (viajes.isNotEmpty()) {
                    return@withContext viajes
                } else {
                    return@withContext listOf()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf<Viaje>()
        }
    }


}