package com.gotravel.mobile.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Serializable
data class Viaje (
    val id: Int?,
    val nombre: String,
    val descripcion: String?,
    private val fechaInicio: String,
    private val fechaFin: String?,
    val costeTotal: Double,
) {
    val inicio: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val formatoFromDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatoFinal = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            return LocalDate.parse(fechaInicio, formatoFromDb).format(formatoFinal)
        }

    val final: String?
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            if(fechaFin != null){
                val formatoFromDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatoFinal = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                return LocalDate.parse(fechaFin, formatoFromDb).format(formatoFinal)
            } else {
                return null
            }
        }
}