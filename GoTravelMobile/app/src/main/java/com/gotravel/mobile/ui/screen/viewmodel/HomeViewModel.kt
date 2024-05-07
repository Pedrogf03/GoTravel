package com.gotravel.mobile.ui.screen.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Viaje
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

sealed interface HomeUiState {
    data class Success(val viaje: Viaje?) : HomeUiState
    object Error : HomeUiState
    object Loading : HomeUiState
}
class HomeViewModel: ViewModel() {

    var uiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getProximoViaje()
    }

    fun getProximoViaje() {
        viewModelScope.launch {
            uiState = try {
                HomeUiState.Success(findProximoViajeByUsuarioId())
            } catch (e: IOException) {
                HomeUiState.Error
            }
        }
    }

    private suspend fun findProximoViajeByUsuarioId() : Viaje? {
        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {
                val salida = DataOutputStream(AppUiState.socket.getOutputStream())
                val entrada = DataInputStream(AppUiState.socket.getInputStream())

                salida.writeUTF("proximoViaje;${AppUiState.usuario.id}")
                salida.flush()

                val jsonFromServer = entrada.readUTF()
                val viaje : Viaje? = gson.fromJson(jsonFromServer, Viaje::class.java)
                if (viaje != null) {
                    return@withContext viaje
                } else {
                    return@withContext null
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext null
        }
    }

}