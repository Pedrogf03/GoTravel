package com.gotravel.mobile.data.model

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.math.BigDecimal

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
data class Ubicacion(
    override val id: Int,
    val coordenadaX: BigDecimal,
    val coordenadaY: BigDecimal
) : Localizacion(id)