package com.gotravel.mobile.ui.screen.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.AppRepository
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

sealed interface HomeUiState {
    data class Success(val proximoViaje: Viaje?, val viajeActual: Viaje?, val imagen1: ImageBitmap) : HomeUiState
    object Error : HomeUiState
    object Loading : HomeUiState
}
class HomeViewModel(
    private val repository: AppRepository
): ViewModel() {

    var uiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getContent()
    }

    private fun getContent() {
        viewModelScope.launch {
            uiState = try {
                HomeUiState.Success(findProximoViajeByUsuarioId(), findViajeActualByUsuarioId(), repository.getHomeImage())
            } catch (e: IOException) {
                HomeUiState.Error
            }
        }
    }

    private suspend fun findProximoViajeByUsuarioId() : Viaje? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findByUserId;proximoViaje")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    return@withContext gson.fromJson(jsonFromServer, Viaje::class.java)

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

    private suspend fun findViajeActualByUsuarioId() : Viaje? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {

                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {
                    val salida = DataOutputStream(Sesion.socket!!.getOutputStream())
                    val entrada = DataInputStream(Sesion.socket!!.getInputStream())

                    salida.writeUTF("findByUserId;viajeActual")
                    salida.flush()

                    val jsonFromServer = entrada.readUTF()
                    val viaje : Viaje? = gson.fromJson(jsonFromServer, Viaje::class.java)
                    if (viaje != null) {
                        return@withContext viaje
                    } else {
                        return@withContext null
                    }

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