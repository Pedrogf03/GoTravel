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
import com.gotravel.mobile.data.model.Metodopago
import com.gotravel.mobile.data.model.Tarjetacredito
import com.gotravel.mobile.ui.screen.SuscripcionDestination
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.CreditCardPaymentClient
import com.gotravel.mobile.ui.utils.MetodopagoAdapter
import com.gotravel.mobile.ui.utils.Regex
import com.gotravel.mobile.ui.utils.obtenerCodigoPais
import com.gotravel.mobile.ui.utils.obtenerTipoTarjeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

sealed interface SuscripcionUiState {
    data class Success(val metodosPago: List<Metodopago>) : SuscripcionUiState
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
        getMetodosPago()
    }

    private fun getMetodosPago() {
        viewModelScope.launch {
            uiState = try {
                SuscripcionUiState.Success(findMetodosPagoFromUserId())
            } catch (e: IOException) {
                SuscripcionUiState.Error
            }
        }
    }

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
    fun suscribirse(context: Context) {

        CreditCardPaymentClient(context).createSubscription()

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

    fun validarDireccion(calle: String, ciudad: String, estado: String, pais: String, cp: String): DirFacturacion? {

        if(calle.matches(Regex.regexCalle)) {

            if(ciudad.matches(Regex.regexCiudadEstado)) {

                if(estado.matches(Regex.regexCiudadEstado)) {

                    val codigoPais = obtenerCodigoPais(pais)

                    if(codigoPais != null) {

                        if(cp.matches(Regex.regexCP)) {

                            return DirFacturacion(line1 = calle, ciudad = ciudad, estado = estado, codigoPais = codigoPais, cp = cp)

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

        } else {
            mensajeUi.postValue("La calle no es válida")
        }

        return null

    }

}