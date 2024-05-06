package com.gotravel.mobile.data.model

import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
data class Direccion(
    override val id: Int,
    val calle: String,
    val numero: String,
    val ciudad: String,
    val estado: String,
    val cp: String
) : Localizacion(id)