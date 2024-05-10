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
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.time.LocalDate


class CrearViajeViewModel : ViewModel() {

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.O)
    fun crearViaje(viaje: Viaje) : Viaje? {

        if(viaje.nombre.isBlank() || viaje.final.isBlank() || viaje.inicio.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos obligatorios")
        } else {
            val fechaInicio: LocalDate = LocalDate.parse(viaje.inicio, formatoFinal)
            val fechaFinal: LocalDate = LocalDate.parse(viaje.final, formatoFinal)

            if(!viaje.nombre.matches(Regex.regexNombre)) {
                mensajeUi.postValue("El nombre no es válido")
            } else if(!viaje.descripcion.isNullOrBlank() && !viaje.descripcion.matches(Regex.regexNombre)) {
                mensajeUi.postValue("La descripción no es válida")
            } else if (fechaFinal.isBefore(fechaInicio)) {
                mensajeUi.postValue("La fecha de final no puede ser antes que la fecha de inicio")
            } else {
                return guardarViaje(viaje)
            }
        }

        return null

    }

    private fun guardarViaje(viaje: Viaje) : Viaje? {

        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        try {

            val salida = DataOutputStream(AppUiState.socket.getOutputStream())
            val entrada = DataInputStream(AppUiState.socket.getInputStream())

            salida.writeUTF("save;${AppUiState.usuario.id};viaje")
            salida.flush()

            val json = gson.toJson(viaje)
            salida.writeUTF(json)
            salida.flush()

            val jsonFromServer = entrada.readUTF()
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