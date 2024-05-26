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
data class Suscripcion (
    val id: String,
    val fechaInicio: String,
    val fechaFinal: String,
    val estado: String,
    val pago: Pago,
)