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
data class Etapa(
    val id: Int? = null,
    val nombre: String,
    val fechaInicio: String,
    val fechaFinal: String,
    val costeTotal: Double,
    val tipo: String
) {
    val contrataciones: List<Contratacion>? = null

    val inicio: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return LocalDate.parse(fechaInicio, formatoFromDb).format(formatoFinal)
        }

    val final: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return LocalDate.parse(fechaFinal, formatoFromDb).format(formatoFinal)
        }

}