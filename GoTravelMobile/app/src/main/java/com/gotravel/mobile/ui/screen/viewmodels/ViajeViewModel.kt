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
import com.gotravel.mobile.data.model.Imagen
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.ViajeDestination
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.Sesion
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

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findById;viaje;${idViaje}")
                    Sesion.salida.flush()

                    val viajeFromServer = Sesion.entrada.readUTF()
                    val viaje = gson.fromJson(viajeFromServer, Viaje::class.java)

                    for(e in viaje.etapas) {
                        Sesion.salida.writeUTF("findContratacionesByEtapa;${e.id}")
                        Sesion.salida.flush()

                        val contratacionesFromServer = Sesion.entrada.readUTF()
                        val type = object : TypeToken<List<Servicio>>() {}.type
                        val servicios =  gson.fromJson<List<Servicio>>(contratacionesFromServer, type)

                        for(s in servicios) {
                            s.imagenes = listOf(getFirstImagenFromServicio(s.id!!)!!)
                        }

                        e.contrataciones = servicios

                    }

                    return@withContext viaje

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
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

    private suspend fun getFirstImagenFromServicio(id: Int): Imagen? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findByServicioId;imagen;$id;one")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val imagen = gson.fromJson(jsonFromServer, Imagen::class.java)

                    if(imagen != null) {
                        val length = Sesion.entrada.readInt() // Lee la longitud del ByteArray
                        val byteArray = ByteArray(length)
                        Sesion.entrada.readFully(byteArray) // Lee el ByteArray
                        imagen.imagen = byteArray
                    }

                    return@withContext imagen

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
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
    suspend fun guardarEtapa(nombre: String, fechaInicio: String, fechaFinal: String, tipo: String, pais: String, etapaActualizar: Etapa? = null): Boolean {

        if(nombre.isBlank() || fechaInicio.isBlank() || fechaFinal.isBlank() || tipo.isBlank() || pais.isBlank()) {
            mensajeUi.postValue("Por favor rellena todos los campos")
        } else {

            val etapa: Etapa
            if(etapaActualizar != null) {
                etapa = etapaActualizar.copy(nombre = nombre, fechaInicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb), fechaFinal = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb), tipo = tipo, pais = pais)
            } else {
                etapa = Etapa(nombre = nombre, fechaInicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb), fechaFinal = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb), tipo = tipo, costeTotal = 0.0, pais = pais)
            }

            if(!(etapa.nombre.isBlank() || etapa.nombre.isEmpty()) && etapa.nombre.matches(Regex.regexCamposAlfaNum) && etapa.nombre.length <= 45) {

                val inicio = LocalDate.parse(etapa.inicio, formatoFinal)
                val final = LocalDate.parse(etapa.final, formatoFinal)

                if(!final.isBefore(inicio)) {

                    val viaje = findViajeById(idViaje)

                    if(!inicio.isBefore(LocalDate.parse(viaje!!.inicio, formatoFinal))) {

                        if(!final.isAfter(LocalDate.parse(viaje.final, formatoFinal))) {

                            for (etapaExistente in viaje.etapas) {
                                if(etapaExistente.id != etapa.id) {
                                    val inicioExistente = LocalDate.parse(etapaExistente.inicio, formatoFinal)
                                    val finalExistente = LocalDate.parse(etapaExistente.final, formatoFinal)

                                    val inicioNueva = LocalDate.parse(fechaInicio, formatoFinal)
                                    val finalNueva = LocalDate.parse(fechaFinal, formatoFinal)

                                    if (inicioNueva < finalExistente && inicioExistente < finalNueva) {
                                        mensajeUi.postValue("La nueva etapa se superpone con una etapa existente")
                                        return false
                                    }
                                }
                            }

                            if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                                return withContext(Dispatchers.IO) {
                                    val gson = GsonBuilder()
                                        .serializeNulls()
                                        .setLenient()
                                        .create()

                                    val etapaFromServer : Etapa?

                                    try {

                                        if(etapaActualizar != null){
                                            Sesion.salida.writeUTF("update;etapa;${idViaje}")
                                        } else{
                                            Sesion.salida.writeUTF("save;etapa;${idViaje}")
                                        }
                                        val json = gson.toJson(etapa)
                                        Sesion.salida.writeUTF(json)
                                        Sesion.salida.flush()

                                        val jsonFromServer = Sesion.entrada.readUTF()
                                        etapaFromServer = gson.fromJson(jsonFromServer, Etapa::class.java)

                                        return@withContext etapaFromServer != null

                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        Sesion.socket!!.close()
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


                if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                    return withContext(Dispatchers.IO) {
                        val viajeActualizado = viaje.copy(nombre = nombre, descripcion = descripcion.ifBlank { null }, fechaInicio = inicio.format(formatoFromDb), fechaFin = fin.format(formatoFromDb))

                        val gson = GsonBuilder()
                            .serializeNulls()
                            .setLenient()
                            .create()

                        val viajeFromServer : Viaje?

                        try {

                            Sesion.salida.writeUTF("update;viaje")
                            Sesion.salida.flush()

                            val json = gson.toJson(viajeActualizado)
                            Sesion.salida.writeUTF(json)
                            Sesion.salida.flush()

                            val jsonFromServer = Sesion.entrada.readUTF()
                            viajeFromServer = gson.fromJson(jsonFromServer, Viaje::class.java)

                            return@withContext viajeFromServer != null

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Sesion.socket!!.close()
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