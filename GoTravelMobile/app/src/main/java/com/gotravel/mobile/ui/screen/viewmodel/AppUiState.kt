package com.gotravel.mobile.ui.screen.viewmodel

import com.gotravel.mobile.data.model.Rol
import com.gotravel.mobile.data.model.Usuario
import java.net.Socket

// Valores por defecto que se cambian al iniciar sesion
object AppUiState {
    var socket: Socket = Socket()
    var usuario: Usuario = Usuario(0, "", null, "", "", listOf(Rol("")))
}