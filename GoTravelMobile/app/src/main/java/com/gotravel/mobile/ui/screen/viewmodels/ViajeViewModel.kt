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
        getViaje()
    }

    private fun getViaje() {
        viewModelScope.launch {
            uiState = try {
                val viaje = findViajeById(idViaje)
                if(viaje != null) {
                    ViajeUiState.Success(viaje)
                } else {
                    ViajeUiState.Error
                }
            } catch (e: IOException) {
                ViajeUiState.Error
            }
        }
    }

    private suspend fun findViajeById(idViaje: Int) : Viaje? {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    AppUiState.salida.writeUTF("findById;viaje;${idViaje}")
                    AppUiState.salida.flush()

                    val jsonFromServer = AppUiState.entrada.readUTF()
                    return@withContext gson.fromJson(jsonFromServer, Viaje::class.java)

                } catch (e: IOException) {
                    e.printStackTrace()
                    AppUiState.socket!!.close()
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun guardarEtapa(nombre: String, fechaInicio: String, fechaFinal: String, tipo: String, etapaActualizar: Etapa? = null): Boolean {

        if(nombre.isBlank() || fechaInicio.isBlank() || fechaFinal.isBlank() || tipo.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos")
        } else {

            val etapa: Etapa
            if(etapaActualizar != null) {
                etapa = etapaActualizar.copy(nombre = nombre, fechaInicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb), fechaFinal = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb), tipo = tipo)
            } else {
                etapa = Etapa(nombre = nombre, fechaInicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb), fechaFinal = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb), tipo = tipo, costeTotal = 0.0)
            }

            if(!(etapa.nombre.isBlank() || etapa.nombre.isEmpty()) && etapa.nombre.matches(Regex.regexCamposAlfaNum) && etapa.nombre.length <= 45) {

                val inicio = LocalDate.parse(etapa.inicio, formatoFinal)
                val final = LocalDate.parse(etapa.final, formatoFinal)

                if(!final.isBefore(inicio)) {

                    val viaje = findViajeById(idViaje)

                    if(!inicio.isBefore(LocalDate.parse(viaje!!.inicio, formatoFinal))) {

                        if(!final.isAfter(LocalDate.parse(viaje.final, formatoFinal))) {

                            for (etapaExistente in viaje.etapas) {
                                val inicioExistente = LocalDate.parse(etapaExistente.inicio, formatoFinal)
                                val finalExistente = LocalDate.parse(etapaExistente.final, formatoFinal)

                                val inicioNueva = LocalDate.parse(fechaInicio, formatoFinal)
                                val finalNueva = LocalDate.parse(fechaFinal, formatoFinal)

                                if (inicioNueva < finalExistente && inicioExistente < finalNueva) {
                                    mensajeUi.postValue("La nueva etapa se superpone con una etapa existente")
                                    return false
                                }
                            }

                            if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
                                return withContext(Dispatchers.IO) {
                                    val gson = GsonBuilder()
                                        .serializeNulls()
                                        .setLenient()
                                        .create()

                                    val etapaFromServer : Etapa?

                                    try {

                                        AppUiState.salida.writeUTF("save;etapa;${idViaje}")
                                        AppUiState.salida.flush()

                                        val json = gson.toJson(etapa)
                                        AppUiState.salida.writeUTF(json)
                                        AppUiState.salida.flush()

                                        val jsonFromServer = AppUiState.entrada.readUTF()
                                        etapaFromServer = gson.fromJson(jsonFromServer, Etapa::class.java)

                                        return@withContext etapaFromServer != null

                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        AppUiState.socket!!.close()
                                        mensajeUi.postValue("No se puede conectar con el servidor")
                                        return@withContext false
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    return@withContext false
                                }
                            } else {
                                return false
                            }

                        } else {
                            mensajeUi.postValue("El final no puede ser posterior a la fecha de final del viaje")
                        }

                    } else {
                        mensajeUi.postValue("El inicio de la etapa no puede ser antes que la fecha de inicio del viaje")
                    }

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
    suspend fun actualizarViaje(nombre: String, descripcion: String, fechaInicio: String, fechaFin: String, viaje: Viaje): Boolean {

        if(nombre.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos obligatorios")
        } else {

            val inicio = LocalDate.parse(fechaInicio, formatoFinal)
            val fin = LocalDate.parse(fechaFin, formatoFinal)

            if(!nombre.matches(Regex.regexCamposAlfaNum) || nombre.length >= 45) {
                mensajeUi.postValue("El nombre no es válido")
            } else if(descripcion.isNotBlank() && !descripcion.matches(Regex.regexCamposAlfaNum)) {
                mensajeUi.postValue("La descripción no es válida")
            } else if (fin.isBefore(inicio)) {
                mensajeUi.postValue("La fecha de final no puede ser antes que la fecha de inicio")
            } else {


                if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
                    return withContext(Dispatchers.IO) {
                        val viajeActualizado = viaje.copy(nombre = nombre, descripcion = descripcion.ifBlank { null }, fechaInicio = inicio.format(formatoFromDb), fechaFin = fin.format(formatoFromDb))

                        val gson = GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create()

                        val viajeFromServer : Viaje?

                        try {

                            AppUiState.salida.writeUTF("update;viaje")
                            AppUiState.salida.flush()

                            val json = gson.toJson(viajeActualizado)
                            AppUiState.salida.writeUTF(json)
                            AppUiState.salida.flush()

                            val jsonFromServer = AppUiState.entrada.readUTF()
                            viajeFromServer = gson.fromJson(jsonFromServer, Viaje::class.java)

                            return@withContext viajeFromServer != null

                        } catch (e: IOException) {
                            e.printStackTrace()
                            AppUiState.socket!!.close()
                            mensajeUi.postValue("No se puede conectar con el servidor")
                            return@withContext false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return@withContext false
                    }
                } else {
                    return false
                }

            }
        }

        return false

    }

}