package com.gotravel.clienteMovil.data

import kotlinx.serialization.Serializable
import lombok.NonNull
import lombok.RequiredArgsConstructor

@Serializable
class Viaje (

    val id: Int,
    val nombre: String,
    val descripcion: String?,
    val fechaInicio: String,
    val fechaFin: String?,
    val costeTotal: Double

)