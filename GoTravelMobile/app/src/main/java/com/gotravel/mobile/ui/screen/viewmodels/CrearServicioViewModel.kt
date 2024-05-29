package com.gotravel.mobile.ui.screen.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Direccion
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.data.model.Tiposervicio
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.Sesion
import com.gotravel.mobile.ui.utils.formatoFinal
import com.gotravel.mobile.ui.utils.formatoFromDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

sealed interface CrearServicioUiState {
    data class Success(val tiposServicio: List<Tiposervicio>) : CrearServicioUiState
    object Error : CrearServicioUiState
    object Loading : CrearServicioUiState
}


class CrearServicioViewModel : ViewModel() {

    var uiState: CrearServicioUiState by mutableStateOf(CrearServicioUiState.Loading)
        private set

    init {
        getTiposServicio()
    }

    private fun getTiposServicio() {
        viewModelScope.launch {
            uiState = try {
                val tiposServicio = findAllTipoServicio()
                if(tiposServicio != null) {
                    CrearServicioUiState.Success(tiposServicio)
                } else {
                    CrearServicioUiState.Error
                }
            } catch (e: IOException) {
                CrearServicioUiState.Error
            }
        }
    }

    private suspend fun findAllTipoServicio(): List<Tiposervicio>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findAll;tipoServicio")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val type = object : TypeToken<List<Tiposervicio>>() {}.type
                    return@withContext gson.fromJson<List<Tiposervicio>>(jsonFromServer, type)

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

    val mensajeUi: MutableLiveData<String> = MutableLiveData()

    fun validarDireccion(linea1: String, linea2: String?, ciudad: String, estado: String, pais: String, cp: String): Direccion? {

        if(linea1.matches(Regex.regexCamposAlfaNum) && linea1.length <= 200) {

            if(linea2 == null || linea2.matches(Regex.regexCamposAlfaNum) && linea2.length <= 200) {

                if(ciudad.matches(Regex.regexCamposAlfaNum) && ciudad.length <= 100) {

                    if(estado.matches(Regex.regexCamposAlfaNum) && estado.length <= 100) {

                        if(pais.matches(Regex.regexCamposAlfaNum) && pais.length <= 100) {

                            if(cp.matches(Regex.regexCp)) {

                                mensajeUi.postValue("")
                                return Direccion(linea1 = linea1, linea2 = linea2, ciudad = ciudad, cp = cp, estado = estado, pais = pais)

                            } else {
                                mensajeUi.postValue("El código postal no es válido")
                            }

                        } else {
                            mensajeUi.postValue("El país no es válido")
                        }

                    } else {
                        mensajeUi.postValue("El estado no es válido")
                    }

                } else {
                    mensajeUi.postValue("La ciudad no es válida")
                }

            } else {
                mensajeUi.postValue("La linea 2 no es válida")
            }

        } else {
            mensajeUi.postValue("La linea 1 no es válida")
        }

        return null

    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun crearServicio(nombre: String, descripcion: String?, precio: Double, fechaInicio: String, fechaFinal: String?, hora: String?, tipoServicio: Tiposervicio, direccion: Direccion): Servicio? {

        val inicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb)
        var final : String? = null
        if(fechaFinal != null) {
            final = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb)
        }

        val servicio = Servicio(nombre = nombre, descripcion = descripcion, precio = precio, fechaInicio = inicio, fechaFinal = final, hora = hora, tipoServicio = tipoServicio, direccion = direccion)

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("save;servicio")
                    Sesion.salida.flush()

                    Sesion.salida.writeUTF(gson.toJson(servicio))
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    return@withContext gson.fromJson(jsonFromServer, Servicio::class.java)

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

    fun validarInfoBasica(
        nombre: String,
        descripcion: String?,
        precio: String,
        tipoServicio: Tiposervicio?
    ): Boolean {

        if(nombre.isBlank() || precio.isBlank() || tipoServicio == null) {
            mensajeUi.postValue("Rellena los campos obligatorios")
            return false
        } else {
            if(nombre.matches(Regex.regexCamposAlfaNum) && nombre.length <= 45) {

                if(descripcion == null || descripcion.matches(Regex.regexCamposAlfaNum)) {

                    try {
                        val coste = precio.replace(",", ".")
                        coste.toDouble()
                        mensajeUi.postValue("")
                        return true
                    } catch (e: NumberFormatException) {
                        mensajeUi.postValue("El precio no es válido")
                        return false
                    }

                } else {
                    mensajeUi.postValue("La descripción no es válida")
                    return false
                }

            } else {
                mensajeUi.postValue("El nombre no es válido")
                return false
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validarFechasYHora(
        fechaInicio: String,
        fechaFinal: String?,
        hora: String?
    ): Boolean {

        if(fechaInicio.isBlank()) {
            mensajeUi.postValue("El servicio tiene que tener la fecha de inicio")
            return false
        } else {
            if(fechaFinal == null && hora == null){

                mensajeUi.postValue("El servicio tiene que tener la fecha de final o la hora")
                return false

            } else if (fechaFinal != null && hora != null){

                mensajeUi.postValue("El servicio no puede tener ambas fecha de final y hora")
                return false

            } else if(fechaFinal != null){

                val inicio = LocalDate.parse(fechaInicio, formatoFinal)
                val fin = LocalDate.parse(fechaFinal, formatoFinal)

                if(fin.isBefore(inicio)) {
                    mensajeUi.postValue("La fecha de final no puede ser antes que la fecha de inicio")
                    mensajeUi.postValue("")
                    return false
                } else {
                    return true
                }

            } else if(hora != null) {
                return true
            }
        }

        return false

    }

    suspend fun actualizarServicio(servicio: Servicio): Servicio? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("update;servicio")
                    Sesion.salida.flush()

                    Sesion.salida.writeUTF(gson.toJson(servicio))
                    Sesion.salida.flush()

                    println(gson.toJson(servicio))

                    val jsonFromServer = Sesion.entrada.readUTF()
                    return@withContext gson.fromJson(jsonFromServer, Servicio::class.java)

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

}