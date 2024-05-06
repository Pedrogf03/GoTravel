package com.gotravel.mobile.data.model

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.LocalDate

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
data class Tarjetacredito(
    override val id: Int,
    override val usuario: Usuario,
    val metodopago: Metodopago,
    val fechaVencimiento: LocalDate,
    val nombre: String,
    val ultimosDigitos: String
) : Metodopago(id, usuario)