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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate


class CrearViajeViewModel : ViewModel() {

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun crearViaje(nombre: String, descripcion: String, fechaInicio: String, fechaFin: String, ) : Viaje? {

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
                return guardarViaje(viaje)
            }
        }

        return null

    }

    private suspend fun guardarViaje(viaje: Viaje) : Viaje? {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
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
                        return@withContext viajeFromServer
                    } else {
                        mensajeUi.postValue("No se ha podido guardar el viaje")
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    AppUiState.socket!!.close()
                    mensajeUi.postValue("No se puede conectar con el servidor")
                    return@withContext null
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return@withContext null
            }
        } else {
            return null
        }

    }

}