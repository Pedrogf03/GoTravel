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

sealed interface ViajesUiState {
    data class Success(val usuario: Usuario, val viajes: List<Viaje>) : ViajesUiState
    object Error : ViajesUiState
    object Loading : ViajesUiState
}

class ViajesViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: Repository
) : ViewModel() {

    private val usuarioId: Int = checkNotNull(savedStateHandle[ViajesDestination.usuarioId])

    private val busqueda: String? = savedStateHandle[ViajesDestination.busqueda]

    var viajesUiState: ViajesUiState by mutableStateOf(ViajesUiState.Loading)
        private set

    init {
        findUsuarioById()
    }

    fun findUsuarioById() {
        viewModelScope.launch {
            try {
                val usuario = repository.findUsuarioById(usuarioId)
                val viajes = mutableListOf<Viaje>()
                val allViajes = repository.findViajesFromUserId(usuarioId)
                if(busqueda != null) {
                    for(viaje in allViajes) {
                        if(viaje.nombre.lowercase().contains(busqueda.lowercase())){
                            viajes.add(viaje)
                        }
                    }
                } else {
                    viajes.addAll(allViajes)
                }
                viajesUiState = ViajesUiState.Success(usuario, viajes)
            } catch (e: IOException) {
                println(e.message)
                viajesUiState = ViajesUiState.Error
            }
        }
    }

}