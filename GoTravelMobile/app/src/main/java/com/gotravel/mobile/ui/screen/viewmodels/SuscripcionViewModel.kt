package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
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
import com.gotravel.mobile.data.model.DirFacturacion
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.SuscripcionDestination
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.PayPalSubscriptions
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.obtenerCodigoPais
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.NullPointerException

sealed interface SuscripcionUiState {
    data class Success(val direcciones: List<DirFacturacion>) : SuscripcionUiState
    object Error : SuscripcionUiState
    object Loading : SuscripcionUiState
}


class SuscripcionViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val esProfesional: Boolean = checkNotNull(savedStateHandle[SuscripcionDestination.esProfesional])

    var mensajeUi: MutableLiveData<String> = MutableLiveData()

    var uiState: SuscripcionUiState by mutableStateOf(SuscripcionUiState.Loading)
        private set

    init {
        getDireccionesFacturacion()
    }

    private fun getDireccionesFacturacion() {
        viewModelScope.launch {
            uiState = try {
                SuscripcionUiState.Success(getDireccionesFacturacionFromUserId())
            } catch (e: IOException) {
                SuscripcionUiState.Error
            }
        }
    }

    private suspend fun getDireccionesFacturacionFromUserId(): List<DirFacturacion> {

        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {

                AppUiState.salida.writeUTF("findByUserId;dirFacturacion")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                val type = object : TypeToken<List<DirFacturacion>>() {}.type
                return@withContext gson.fromJson<List<DirFacturacion>?>(jsonFromServer, type)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf()
        }

    }

    suspend fun guardarDireccion(linea1: String, linea2: String?, ciudad: String, estado: String, pais: String, cp: String): Boolean {

        if(linea1.matches(Regex.regexDireccion)) {

            if(linea2 != null && !linea2.matches(Regex.regexDireccion)){
                mensajeUi.postValue("La linea2 no es válida")
            } else {
                if(ciudad.matches(Regex.regexCiudadEstado)) {

                    if(estado.matches(Regex.regexCiudadEstado)) {

                        val codigoPais = obtenerCodigoPais(pais)

                        if(codigoPais != null) {

                            if(cp.matches(Regex.regexCP)) {

                                var dirFacturacion = DirFacturacion(linea1 = linea1, linea2 = linea2, ciudad = ciudad, estado = estado, codigoPais = codigoPais, cp = cp)

                                return withContext(Dispatchers.IO) {
                                    val gson = GsonBuilder()
                                        .serializeNulls()
                                        .setLenient()
                                        .create()

                                    try {

                                        AppUiState.salida.writeUTF("save;dirFacturacion")
                                        AppUiState.salida.flush()

                                        AppUiState.salida.writeUTF(gson.toJson(dirFacturacion))
                                        AppUiState.salida.flush()

                                        println(gson.toJson(dirFacturacion))

                                        val jsonFromServer = AppUiState.entrada.readUTF()
                                        dirFacturacion = gson.fromJson(jsonFromServer, DirFacturacion::class.java)

                                        getDireccionesFacturacion()
                                        return@withContext true

                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    } catch (e: NullPointerException){
                                        mensajeUi.postValue("No se ha podido guardar la direccion")
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    return@withContext false
                                }

                            } else {
                                mensajeUi.postValue("El código postal no es válido")
                            }

                        } else {
                            mensajeUi.postValue("El país no es válido")
                        }

                    } else {
                        mensajeUi.postValue("La provincia no es válida")
                    }

                } else {
                    mensajeUi.postValue("La ciudad no es válida")
                }
            }

        } else {
            mensajeUi.postValue("La linea1 no es válida")
        }

        return false

    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun suscribirse(context: Context, dirFacturacion: DirFacturacion) {
        PayPalSubscriptions(context).createSubscription(dirFacturacion)
    }

    /*

    private suspend fun findMetodosPagoFromUserId(): List<Metodopago> {
        return withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .registerTypeAdapter(Metodopago::class.java, MetodopagoAdapter())
                .serializeNulls()
                .setLenient()
                .create()

            try {

                AppUiState.salida.writeUTF("findMetodosPago")
                AppUiState.salida.flush()

                val jsonFromServer = AppUiState.entrada.readUTF()
                val type = object : TypeToken<List<Metodopago>>() {}.type
                return@withContext gson.fromJson<List<Metodopago>?>(jsonFromServer, type)

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf<Metodopago>()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validarMetodopago(vararg datos: String, dirFacturacion: DirFacturacion? = null): Metodopago? {

        if (datos.size > 1) {

            val numero = datos[0]
            val titular = datos[1]
            val fechaVencimiento = datos[2]
            val cvv = datos[3]

            if(titular.matches(Regex.regexTitular)){
                if(numero.matches(Regex.regexNumero)){
                    if(cvv.matches(Regex.regexCvv)){
                        if(fechaVencimiento.isNotBlank()) {

                            val mesVencimiento = fechaVencimiento.take(2).toInt()
                            val anyoVencimiento = fechaVencimiento.takeLast(2).toInt()

                            val fechaActual = LocalDate.now()
                            val mesActual = fechaActual.monthValue
                            val anyoActual = fechaActual.year % 100 // Obtenemos los dos últimos dígitos del año

                            if (anyoVencimiento > anyoActual || (anyoVencimiento == anyoActual && mesVencimiento >= mesActual)) {

                                return Tarjetacredito(numero = numero, titular = titular, tipo = obtenerTipoTarjeta(numero), mesVencimiento = mesVencimiento, anyoVencimiento = anyoVencimiento, cvv = cvv, dirFacturacion = dirFacturacion!!)

                            } else {
                                println("La fecha de vencimiento no es válida")
                            }

                        } else {
                            mensajeUi.postValue("Introduce la fecha de vencimiento")
                        }
                    } else {
                        mensajeUi.postValue("El código de seguridad de la tarjeta no es válido")
                    }
                } else {
                    mensajeUi.postValue("El numero de la tarjeta no es válido")
                }
            } else {
                mensajeUi.postValue("El titular de la tarjeta no es válido")
            }
        }


        return null

    }

    fun guardarMetodopago(metodo: Metodopago) : Metodopago? {

        val gson = GsonBuilder()
            .registerTypeAdapter(Metodopago::class.java, MetodopagoAdapter())
            .serializeNulls()
            .setLenient()
            .create()

        try {

            AppUiState.salida.writeUTF("save;metodoPago")
            AppUiState.salida.flush()

            val json = gson.toJson(metodo)
            AppUiState.salida.writeUTF(json)
            AppUiState.salida.flush()

            val jsonFromServer = AppUiState.entrada.readUTF()
            val metodoFromServer = gson.fromJson(jsonFromServer, Metodopago::class.java)

            if (metodoFromServer != null) {
                getMetodosPago()
                return metodoFromServer
            } else {
                mensajeUi.postValue("No se ha podido guardar el metodo de pago")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mensajeUi.postValue("No se ha podido guardar el metodo de pago")
        return null

    }
     */

}