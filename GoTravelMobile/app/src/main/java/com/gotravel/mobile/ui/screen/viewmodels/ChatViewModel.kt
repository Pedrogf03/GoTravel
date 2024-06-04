package com.gotravel.mobile.ui.screen.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gotravel.mobile.data.model.Mensaje
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.screen.ChatDestination
import com.gotravel.mobile.ui.utils.Sesion
import com.gotravel.mobile.ui.utils.formatoFromDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

sealed interface ChatUiState {
    data class Success(val mensajes: List<Mensaje>, val otroUsuario: Usuario) : ChatUiState
    object Error : ChatUiState
    object Loading : ChatUiState
}

@RequiresApi(Build.VERSION_CODES.O)
class ChatViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val idOtroUsuario: Int = checkNotNull(savedStateHandle[ChatDestination.idOtroUsuario])

    var uiState: ChatUiState by mutableStateOf(ChatUiState.Loading)
        private set

    private val _mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajes = _mensajes.asStateFlow()

    private var escuchando: Boolean = true

    init {
        getMensajes()
    }


    private fun getMensajes() {
        viewModelScope.launch {
            uiState = try {
                val mensajes = findAllMensajesBetweenUsers()
                val otroUsuario = findUserById()
                if(mensajes != null && otroUsuario != null) {
                    _mensajes.value = mensajes
                    iniciarEscuchaDeMensajes()
                    ChatUiState.Success(mensajes, otroUsuario)
                } else {
                    ChatUiState.Error
                }
            } catch (e: IOException) {
                ChatUiState.Error
            }
        }
    }

    private suspend fun findAllMensajesBetweenUsers(): List<Mensaje>? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findAllMensajesBetweenUsers;${idOtroUsuario}")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val type = object : TypeToken<List<Mensaje>>() {}.type
                    return@withContext gson.fromJson<List<Mensaje>>(jsonFromServer, type)

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

    private suspend fun findUserById(): Usuario? {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            return withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF("findById;usuario;${idOtroUsuario}")
                    Sesion.salida.flush()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    val usuario = gson.fromJson(jsonFromServer, Usuario::class.java)

                    // Si se recibe un true (es decir, el usuario tiene foto asociada en la bbdd)
                    if(Sesion.entrada.readBoolean()) {
                        val length = Sesion.entrada.readInt() // Lee la longitud del ByteArray
                        val byteArray = ByteArray(length)
                        Sesion.entrada.readFully(byteArray) // Lee el ByteArray
                        usuario.foto = byteArray
                    }

                    Sesion.salida.writeUTF("chat;${idOtroUsuario}")
                    Sesion.salida.flush()

                    return@withContext usuario

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
    suspend fun enviarMensaje(texto: String) {

        val mensaje = Mensaje(texto = texto, fecha = LocalDate.now().format(formatoFromDb), hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            withContext(Dispatchers.IO) {
                val gson = GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create()

                try {

                    Sesion.salida.writeUTF(gson.toJson(mensaje))
                    Sesion.salida.flush()

                } catch (e: IOException) {
                    e.printStackTrace()
                    Sesion.socket!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun iniciarEscuchaDeMensajes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                while (escuchando && isActive && Sesion.socket != null && !Sesion.socket!!.isClosed) {
                    val gson = GsonBuilder()
                        .serializeNulls()
                        .setLenient()
                        .create()

                    val jsonFromServer = Sesion.entrada.readUTF()
                    if(jsonFromServer.equals("fin")) {
                        escuchando = false
                    } else {
                        val mensajeFromServer = gson.fromJson(jsonFromServer, Mensaje::class.java)

                        withContext(Dispatchers.Main) {
                            _mensajes.value += mensajeFromServer
                        }
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun pararEscuchaDeMensajes() {

        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
            withContext(Dispatchers.IO) {

                try {
                    Sesion.salida.writeUTF("dejarChat")
                    Sesion.salida.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

    }


}