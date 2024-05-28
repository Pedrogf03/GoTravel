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
data class Direccion (
    val id: Int? = null,
    val linea1: String,
    val linea2: String?,
    val ciudad: String,
    val estado: String,
    val pais: String,
    val cp: String
)