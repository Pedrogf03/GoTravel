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
data class Mensaje (
    val id: Int? = null,
    val emisor: Usuario? = null,
    val receptor: Usuario? = null,
    val texto: String,
    val fecha: String,
    val hora: String,
    val leido: String
)