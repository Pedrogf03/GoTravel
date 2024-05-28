package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.os.Build
import android.provider.Settings.Global
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Suscripcion
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.screen.SuscripcionDestination
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.PayPalSubscriptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface SuscripcionUiState {
    data class Success(val suscripcion: Suscripcion?) : SuscripcionUiState
    object Error : SuscripcionUiState
    object Loading : SuscripcionUiState
}

class SuscripcionViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val esProfesional: Boolean = checkNotNull(savedStateHandle[SuscripcionDestination.esProfesional])

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun suscribirse(context: Context) {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            val suscripcion = findSuscripcionByUsuarioId()

            if(suscripcion != null){
                PayPalSubscriptions(context).activateSubscription(
                    subscriptionId = suscripcion.id,
                    onSuscripcionReactivada = {
                        GlobalScope.launch {
                            activarSuscripcion(suscripcion.id)
                        }
                    }
                )
            } else {
                PayPalSubscriptions(context).createSubscription()
            }
        }

    }

    var uiState: SuscripcionUiState by mutableStateOf(SuscripcionUiState.Loading)
        private set

    init {
        getSuscripcion()
    }

    private fun getSuscripcion() {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            viewModelScope.launch {
                uiState = try {
                    SuscripcionUiState.Success(findSuscripcionByUsuarioId())
                } catch (e: IOException) {
                    SuscripcionUiState.Error
                }
            }
        }

    }

    private suspend fun findSuscripcionByUsuarioId() : Suscripcion? {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    AppUiState.salida.writeUTF("findByUserId;suscripcion")
                    AppUiState.salida.flush()

                    val jsonFromServer = AppUiState.entrada.readUTF()
                    return@withContext gson.fromJson<Suscripcion?>(jsonFromServer, Suscripcion::class.java)

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

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cancelSubscription(context: Context, subscriptionId: String) {
        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            PayPalSubscriptions(context).cancelSubscription(
                subscriptionId = subscriptionId,
                onSuscripcionCancelada = {
                    GlobalScope.launch {
                        cancelarSuscripcion(subscriptionId)
                    }
                }
            )
        }
    }

    private suspend fun cancelarSuscripcion(subscriptionId : String) {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    AppUiState.salida.writeUTF("suscripcion;cancelar;$subscriptionId")

                    val jsonFromServer = AppUiState.entrada.readUTF()
                    val suscripcion : Suscripcion? = gson.fromJson(jsonFromServer, Suscripcion::class.java)

                    if(suscripcion != null) {
                        getSuscripcion()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    AppUiState.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    private suspend fun activarSuscripcion(subscriptionId : String) {

        if(AppUiState.socket != null && !AppUiState.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    AppUiState.salida.writeUTF("suscripcion;renovar;$subscriptionId")

                    val jsonFromServer = AppUiState.entrada.readUTF()
                    val suscripcion : Suscripcion? = gson.fromJson(jsonFromServer, Suscripcion::class.java)

                    if(suscripcion != null) {
                        getSuscripcion()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    AppUiState.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

}