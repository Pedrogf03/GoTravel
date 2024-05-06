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
data class Resena (
    val id: ResenaId,
    val usuario: Usuario,
    val contratacion: Contratacion,
    val puntuacion: Int,
    val contenido: String,
    val oculto: String
)