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
    val idServicio: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false
        val entity = other as ResenaId
        return idUsuario == entity.idUsuario && idServicio == entity.idServicio
    }

    override fun hashCode(): Int {
        return Objects.hash(idUsuario, idServicio)
    }

}