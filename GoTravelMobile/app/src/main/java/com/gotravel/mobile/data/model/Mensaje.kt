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
    val id: Int,
    val contenido: String,
    val fecha: String,
    val hora: String,
    val emisor: Usuario,
    val chat: Chat
)