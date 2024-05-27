package com.gotravel.mobile.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.gotravel.mobile.ui.utils.formatoFromDb
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
data class Suscripcion (
    val id: String,
    val fechaInicio: String,
    val fechaFinal: String,
    val estado: String,
    val renovar: String,
    val pagos: List<Pago>
) {

    val final : String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val fecha = LocalDate.parse(fechaFinal, formatoFromDb)
            return fecha.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy"))
        }

}