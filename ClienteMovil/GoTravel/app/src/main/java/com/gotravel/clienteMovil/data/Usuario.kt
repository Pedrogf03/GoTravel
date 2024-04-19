package com.gotravel.clienteMovil.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter


@Entity(tableName = "Usuario")
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Usuario (

    @PrimaryKey
    @SerialName(value = "idUsuario")
    private val idUsuario: Int,
    @SerialName(value = "nombre")
    private var nombre: String,
    @SerialName(value = "email")
    private var email: String,
    @SerialName(value = "password")
    private var password: String,

)