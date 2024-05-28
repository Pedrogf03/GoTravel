package com.gotravel.mobile.data.model

import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class Servicio (
    val id: Int? = null,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val fechaInicio: String,
    val fechaFinal: String?,
    val hora: String?,
    val tipoServicio: Tiposervicio,
    val direccion: Direccion,
    val usuario: Usuario? = null
)