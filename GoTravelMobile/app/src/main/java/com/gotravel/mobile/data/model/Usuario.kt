package com.gotravel.mobile.data.model

import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val roles: List<Rol>
) {
    val apellidos: String? = null
    val tfno: String? = null
    val foto: ByteArray? = null
    val oculto: String? = null
    val viajes: List<Viaje>? = null
    val servicios: List<Servicio>? = null
    val pagos: List<Pago>? = null
    val contrataciones: List<Contratacion>? = null
    val chatsComoUsuario1: List<Chat>? = null
    val chatsComoUsuario2: List<Chat>? = null
    val mensajes: List<Mensaje>? = null
    val metodosPago: List<Metodopago>? = null
    val chats: List<Chat>
        get() {
            val todosLosChats: MutableList<Chat> = ArrayList()
            todosLosChats.addAll(chatsComoUsuario1!!)
            todosLosChats.addAll(chatsComoUsuario2!!)
            return todosLosChats
        }
}