package com.gotravel.clienteMovil.data

import kotlinx.serialization.Serializable

@Serializable
class Usuario (

    val id: Int,
    val nombre: String,
    val apellidos: String?,
    val email: String,
    val tfno: String?,
    val foto: Array<Byte>?,
    val oculto: String

)