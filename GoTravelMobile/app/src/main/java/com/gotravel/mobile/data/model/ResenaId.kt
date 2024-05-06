package com.gotravel.mobile.data.model

import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.util.Objects

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class ResenaId(
    val idUsuario: Int,
    val idContratacion: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false
        val entity = other as ResenaId
        return idUsuario == entity.idUsuario && idContratacion == entity.idContratacion
    }

    override fun hashCode(): Int {
        return Objects.hash(idUsuario, idContratacion)
    }

    companion object {
        private const val serialVersionUID = 5465454492221406391L
    }
}