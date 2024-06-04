package com.gotravel.mobile.ui.screen.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Suscripcion
import com.gotravel.mobile.ui.screen.SuscripcionDestination
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
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

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            val suscripcion = findSuscripcionByUsuarioId()

            if(suscripcion != null){
                activarSuscripcion(suscripcion.id)
            } else {
                crearSuscripcion(context)
            }
        }

    }

    private suspend fun crearSuscripcion(context: Context) {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            withContext(Dispatchers.IO) {

                try {

                    Sesion.salida.writeUTF("suscripcion;crear")
                    Sesion.salida.flush()

                    val url = Sesion.entrada.readUTF()

                    if(url != null) {
                        // Crea un Intent para abrir el enlace de aprobación en el navegador
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

    var uiState: SuscripcionUiState by mutableStateOf(SuscripcionUiState.Loading)
        private set

    init {
        getSuscripcion()
    }

    private fun getSuscripcion() {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
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

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findByUserId;suscripcion")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    return@withContext gson.fromJson(jsonFromServer, Suscripcion::class.java)

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

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cancelarSuscripcion(subscriptionId: String) {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("suscripcion;cancelar;$subscriptionId")

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val suscripcion : Suscripcion? = gson.fromJson(jsonFromServer, Suscripcion::class.java)

                    if(suscripcion != null) {
                        getSuscripcion()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    private suspend fun activarSuscripcion(subscriptionId : String) {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("suscripcion;renovar;$subscriptionId")

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val suscripcion : Suscripcion? = gson.fromJson(jsonFromServer, Suscripcion::class.java)

                    if(suscripcion != null) {
                        getSuscripcion()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

}