package com.gotravel.mobile.data.model

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
data class Paypal(
    override val id: Int?,
    val email: String
) : Metodopago(id)