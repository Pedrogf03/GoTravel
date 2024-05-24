package com.gotravel.mobile.ui.screen.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Metodopago
import com.gotravel.mobile.ui.screen.SuscripcionDestination
import com.gotravel.mobile.ui.utils.AppUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

sealed interface SuscripcionUiState {
    data class Success(val metodosPago: List<Metodopago>) : SuscripcionUiState
    object Error : SuscripcionUiState
    object Loading : SuscripcionUiState
}


class SuscripcionViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val esProfesional: Boolean = checkNotNull(savedStateHandle[SuscripcionDestination.esProfesional])

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

    suspend fun suscribirse() {

        withContext(Dispatchers.IO) {

            val gson = GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()

            try {
                val salida = DataOutputStream(AppUiState.socket!!.getOutputStream())
                val entrada = DataInputStream(AppUiState.socket!!.getInputStream())

                salida.writeUTF("suscribirme")
                salida.flush()

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

}