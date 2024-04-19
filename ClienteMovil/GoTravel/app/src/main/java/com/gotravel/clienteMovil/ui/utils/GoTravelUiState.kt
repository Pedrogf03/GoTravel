package com.gotravel.clienteMovil.ui.utils

import com.gotravel.clienteMovil.data.Usuario
import java.net.Socket

data class GoTravelUiState(
    val usuario: Usuario? = null,
    val cliente: Socket? = null
)