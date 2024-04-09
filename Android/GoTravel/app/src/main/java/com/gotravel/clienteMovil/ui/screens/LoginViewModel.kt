package com.gotravel.clienteMovil.ui.screens

import androidx.lifecycle.ViewModel
import com.gotravel.clienteMovil.data.Usuario
import com.gotravel.clienteMovil.ui.utils.GoTravelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.net.Socket

class LoginViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(GoTravelUiState())
    val uiState: StateFlow<GoTravelUiState> = _uiState.asStateFlow()

    fun updateUsuario(usuario: Usuario) {
        _uiState.update { currentState ->
            currentState.copy(usuario = usuario)
        }
    }

    fun updateSocket(cliente: Socket) {
        _uiState.update { currentState ->
            currentState.copy(cliente = cliente)
        }
    }

}