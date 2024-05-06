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
data class Pago (
    val id: Int,
    val usuario: Usuario,
    val coste: Double,
    val fecha: String,
    val metodopago: Metodopago
)