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
data class Etapa(
    val id: Int,
    val nombre: String,
    val fechaInicio: String,
    val fechaFinal: String,
    val costeTotal: Double,
    val tipo: String
) {
    val contrataciones: List<Contratacion>? = null
}