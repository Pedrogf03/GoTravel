package com.gotravel.mobile.ui.screen.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Mensaje
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface ChatsUiState {
    data class Success(val conversaciones: Map<Int, Mensaje>) : ChatsUiState
    object Error : ChatsUiState
    object Loading : ChatsUiState
}

class ChatsViewModel: ViewModel() {

    var uiState: ChatsUiState by mutableStateOf(ChatsUiState.Loading)
        private set

    init {
        getConversaciones()
    }

    private fun getConversaciones() {
        viewModelScope.launch {
            uiState = try {
                val conversaciones = findAllMensajesByUsuarioId()
                if(conversaciones != null) {
                    ChatsUiState.Success(conversaciones)
                } else {
                    ChatsUiState.Error
                }
            } catch (e: IOException) {
                ChatsUiState.Error
            }
        }
    }

    private suspend fun findAllMensajesByUsuarioId() : Map<Int, Mensaje>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findAllMensajes")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val type = object : TypeToken<Map<Int, Mensaje>>() {}.type

                    val conversaciones =  gson.fromJson<Map<Int, Mensaje>>(jsonFromServer, type)

                    for (c in conversaciones.entries) {
                        if(c.value.emisor!!.id == Sesion.usuario.id) {
                            c.value.receptor!!.foto = getFotoByUserId(c.value.receptor!!.id)
                        } else {
                            c.value.emisor!!.foto = getFotoByUserId(c.value.emisor!!.id)
                        }
                    }

                    return@withContext conversaciones

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

    private suspend fun getFotoByUserId(id: Int?): ByteArray? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {

                try {

                    Sesion.salida.writeUTF("findImageFromUserId;${id}")
                    Sesion.salida.flush()

                    if(Sesion.entrada.readBoolean()){
                        val length = Sesion.entrada.readInt() // Lee la longitud del ByteArray
                        val byteArray = ByteArray(length)
                        Sesion.entrada.readFully(byteArray) // Lee el ByteArray
                        return@withContext byteArray
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