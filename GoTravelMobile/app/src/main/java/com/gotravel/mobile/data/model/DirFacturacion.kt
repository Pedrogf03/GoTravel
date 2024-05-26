package com.gotravel.mobile.data.model

import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class DirFacturacion (
    val linea1: String,
    val linea2: String? = null,
    val ciudad: String,
    val estado: String,
    val cp: String,
    val codigoPais: String
)