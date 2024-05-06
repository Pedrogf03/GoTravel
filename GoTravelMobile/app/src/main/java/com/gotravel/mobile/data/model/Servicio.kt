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
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val tipoServicio: Tiposervicio,
    val localizacion: Localizacion,
    val usuario: Usuario,
    val oculto: String
)