package com.gotravel.mobile.data.model

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
data class Tarjetacredito(
    override val id: Int? = null,
    val numero: String,
    val titular: String,
    val tipo: String,
    val mesVencimiento: Int,
    val anyoVencimiento: Int,
    val cvv: String,
    val dirFacturacion: DirFacturacion
) : Metodopago(id)