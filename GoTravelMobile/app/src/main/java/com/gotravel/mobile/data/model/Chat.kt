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
data class Chat(
    val id: Int,
    val usuario1: Usuario,
    val usuario2: Usuario,
    val mensajes: List<Mensaje>
)