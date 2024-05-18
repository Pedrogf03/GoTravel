package com.gotravel.mobile.ui.screen.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Etapa
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.ViajeDestination
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.formatoFinal
import com.gotravel.mobile.ui.utils.formatoFromDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

sealed interface ViajeUiState {
    data class Success(val viaje: Viaje) : ViajeUiState
    object Error : ViajeUiState
    object Loading : ViajeUiState
}

class ViajeViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val idViaje: Int = checkNotNull(savedStateHandle[ViajeDestination.idViaje])

    var mensajeUi: MutableLiveData<String> = MutableLiveData()

    var uiState: ViajeUiState by mutableStateOf(ViajeUiState.Loading)
        private set

    init {
        getAllFromViaje()
    }

    fun getAllFromViaje() {
        viewModelScope.launch {
            try {
                val viaje = findViajeById(idViaje)
                if(viaje != null) {
                    val etapasFromViaje = findEtapasFromViajeId(viaje.id!!)
                    viaje.etapas = etapasFromViaje
                    uiState = ViajeUiState.Success(viaje)
                } else {
                    uiState = ViajeUiState.Error
                }
            } catch (e: IOException) {
                uiState = ViajeUiState.Error
            }
        }
    }

    private suspend fun findViajeById(idViaje: Int) : Viaje? {

        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {

                AppUiState.salida.writeUTF("findViajeById;${AppUiState.usuario.id};${idViaje}")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                return@withContext gson.fromJson(jsonFromServer, Viaje::class.java)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext null
        }

    }

    private suspend fun findEtapasFromViajeId(idViaje: Int) : List<Etapa> {

        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {

                AppUiState.salida.writeUTF("findEtapasFromViajeId;${AppUiState.usuario.id};${idViaje}")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                val type = object : TypeToken<List<Etapa>>() {}.type
                return@withContext gson.fromJson(jsonFromServer, type)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarEtapa(etapa: Etapa, nombre: String, fechaInicio: String, fechaFinal: String, tipo: String) : Boolean {

        if(nombre.isBlank() || fechaInicio.isBlank() || fechaFinal.isBlank() || tipo.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos")
        } else {

            val etapaActualizada = etapa.copy(nombre = nombre, fechaInicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb), fechaFinal = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb), tipo = tipo)

            if(!(etapa.nombre.isBlank() || etapa.nombre.isEmpty()) && etapa.nombre.matches(Regex.regexNombre)) {

                val inicio = LocalDate.parse(etapa.inicio, formatoFinal)
                val final = LocalDate.parse(etapa.final, formatoFinal)

                if(!final.isBefore(inicio)) {

                    val gson = GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create()

                    val etapaFromServer : Etapa?

                    try {

                        AppUiState.salida.writeUTF("save;${AppUiState.usuario.id};etapa;${idViaje}")
                        AppUiState.salida.flush()

                        val json = gson.toJson(etapaActualizada)
                        AppUiState.salida.writeUTF(json)
                        AppUiState.salida.flush()

                        val jsonFromServer = AppUiState.entrada.readUTF()
                        etapaFromServer = gson.fromJson(jsonFromServer, Etapa::class.java)

                        return etapaFromServer != null

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return false

                } else {
                    mensajeUi.postValue("La fecha de final no puede ser anterior a la inicial")
                }

            } else {
                mensajeUi.postValue("El nombre no es válido")
            }

            return false

        }

        return false

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun crearEtapa(nombre: String, fechaInicio: String, fechaFinal: String, tipo: String): Boolean {

        if(nombre.isBlank() || fechaInicio.isBlank() || fechaFinal.isBlank() || tipo.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos")
        } else {

            val etapa = Etapa(nombre = nombre, fechaInicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb), fechaFinal = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb), tipo = tipo, costeTotal = 0.0)

            if(!(etapa.nombre.isBlank() || etapa.nombre.isEmpty()) && etapa.nombre.matches(Regex.regexNombre)) {

                val inicio = LocalDate.parse(etapa.inicio, formatoFinal)
                val final = LocalDate.parse(etapa.final, formatoFinal)

                if(!final.isBefore(inicio)) {

                    val gson = GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create()

                    val etapaFromServer : Etapa?

                    try {

                        AppUiState.salida.writeUTF("save;${AppUiState.usuario.id};etapa;${idViaje}")
                        AppUiState.salida.flush()

                        val json = gson.toJson(etapa)
                        AppUiState.salida.writeUTF(json)
                        AppUiState.salida.flush()

                        val jsonFromServer = AppUiState.entrada.readUTF()
                        etapaFromServer = gson.fromJson(jsonFromServer, Etapa::class.java)

                        return etapaFromServer != null

                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return false

                } else {
                    mensajeUi.postValue("La fecha de final no puede ser anterior a la inicial")
                }

            } else {
                mensajeUi.postValue("El nombre no es válido")
            }

            return false

        }

        return false

    }

}