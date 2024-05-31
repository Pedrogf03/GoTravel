package com.gotravel.mobile.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.gotravel.mobile.ui.utils.formatoFinal
import com.gotravel.mobile.ui.utils.formatoFromDb
import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.LocalDate

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class Servicio (
    val id: Int? = null,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val fechaInicio: String,
    val fechaFinal: String?,
    val hora: String?,
    val tipoServicio: Tiposervicio,
    val direccion: Direccion,
    var usuario: Usuario? = null,
    var imagenes: List<Imagen> = listOf(),
    var resenas: List<Resena> = listOf(),
    val publicado: String = "0",
    var contratado: Boolean = false
) {
    val inicio: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return LocalDate.parse(fechaInicio, formatoFromDb).format(formatoFinal)
        }

    val final: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return if (fechaFinal == null) "" else LocalDate.parse(fechaFinal, formatoFromDb).format(formatoFinal)
        }
}