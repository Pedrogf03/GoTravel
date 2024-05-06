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
data class Contratacion(
    val id: Int,
    val servicio: Servicio,
    val usuario: Usuario,
    val fecha: String,
    val etapa: Etapa,
    val pago: Pago
)