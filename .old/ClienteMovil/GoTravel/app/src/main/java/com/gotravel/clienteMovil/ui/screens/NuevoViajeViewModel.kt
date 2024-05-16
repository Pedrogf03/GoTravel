package com.gotravel.clienteMovil.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotravel.clienteMovil.data.Repository
import com.gotravel.clienteMovil.data.Usuario
import com.gotravel.clienteMovil.data.Viaje
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


sealed interface NuevoViajeUiState {
    data class Success(val usuario: Usuario) : NuevoViajeUiState
    object Error : NuevoViajeUiState
    object Loading : NuevoViajeUiState
}

class NuevoViajeViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: Repository
) : ViewModel() {

    private val usuarioId: Int = checkNotNull(savedStateHandle[NuevoViajeDestination.usuarioId])

    var nuevoViajeUiState: NuevoViajeUiState by mutableStateOf(NuevoViajeUiState.Loading)
        private set

    init {
        findUsuarioById()
    }

    fun findUsuarioById() {
        viewModelScope.launch {
            try {
                val usuario = repository.findUsuarioById(usuarioId)
                nuevoViajeUiState = NuevoViajeUiState.Success(usuario)
            } catch (e: IOException) {
                println(e.message)
                nuevoViajeUiState = NuevoViajeUiState.Error
            }
        }
    }

    suspend fun saveViaje(viaje: Viaje) : Boolean {
        var guardado = false
        viewModelScope.launch {
            try {
                guardado = repository.saveViaje(viaje, usuarioId)
            } catch (e: Exception) {
                guardado = false
            }
        }.join()
        return guardado
    }


}