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
data class Viaje (
    val id: Int? = null,
    val nombre: String,
    val descripcion: String?,
    val fechaInicio: String,
    val fechaFin: String,
    val costeTotal: Double,
) {
    val inicio: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return LocalDate.parse(fechaInicio, formatoFromDb).format(formatoFinal)
        }

    val final: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return LocalDate.parse(fechaFin, formatoFromDb).format(formatoFinal)
        }
}