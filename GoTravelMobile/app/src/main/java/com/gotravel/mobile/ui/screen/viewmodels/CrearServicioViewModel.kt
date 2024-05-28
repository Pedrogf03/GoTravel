package com.gotravel.mobile.ui.screen.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.formatoFinal
import com.gotravel.mobile.ui.utils.formatoFromDb
import java.io.IOException
import java.time.LocalDate


class CrearServicioViewModel : ViewModel() {

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.O)
    fun crearServicio(nombre: String, descripcion: String, fechaInicio: String, fechaFin: String, ) : Viaje? {

        if(nombre.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos obligatorios")
        } else {

            val inicio = LocalDate.parse(fechaInicio, formatoFinal)
            val fin = LocalDate.parse(fechaFin, formatoFinal)

            if(!nombre.matches(Regex.regexNombre)) {
                mensajeUi.postValue("El nombre no es válido")
            } else if(descripcion.isNotBlank() && !descripcion.matches(Regex.regexNombre)) {
                mensajeUi.postValue("La descripción no es válida")
            } else if (fin.isBefore(inicio)) {
                mensajeUi.postValue("La fecha de final no puede ser antes que la fecha de inicio")
            } else {
                val viaje = Viaje(nombre = nombre, descripcion = descripcion.ifBlank { null }, fechaInicio = inicio.format(formatoFromDb), fechaFin = fin.format(formatoFromDb), costeTotal = 0.0)
                return guardarServicio(viaje)
            }
        }

        return null

    }

    private fun guardarServicio(viaje: Viaje) : Viaje? {

        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        try {

            AppUiState.salida.writeUTF("save;viaje")
            AppUiState.salida.flush()

            val json = gson.toJson(viaje)
            AppUiState.salida.writeUTF(json)
            AppUiState.salida.flush()

            val jsonFromServer = AppUiState.entrada.readUTF()
            val viajeFromServer = gson.fromJson(jsonFromServer, Viaje::class.java)

            if (viajeFromServer != null) {
                return viajeFromServer
            } else {
                mensajeUi.postValue("No se ha podido guardar el viaje")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

    }

}