package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Mensaje
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ChatUiState
import com.gotravel.mobile.ui.screen.viewmodels.ChatViewModel
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object ChatDestination : NavDestination {
    override val route = "chat"
    override val titleRes = R.string.app_name
    const val idOtroUsuario = "idOtroUsuario"
    val routeWithArgs = "$route/{$idOtroUsuario}"
}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateUp: () -> Unit,
    navigateToStart: () -> Unit
) {

    when(val uiState = viewModel.uiState){
        is ChatUiState.Loading -> {
            LandingLoadingScreen()
        }
        is ChatUiState.Success -> {

            BackHandler(enabled = true) {
                GlobalScope.launch {
                    viewModel.pararEscuchaDeMensajes()
                    withContext(Dispatchers.Main) {
                        navigateUp()
                    }
                }
            }

            Scaffold(
                topBar = {
                    AppTopBar(
                        title = uiState.otroUsuario.nombre,
                        canNavigateBack = true,
                        navigateUp = {
                            GlobalScope.launch {
                                viewModel.pararEscuchaDeMensajes()
                                withContext(Dispatchers.Main) {
                                    navigateUp()
                                }
                            }
                        },
                        image = uiState.otroUsuario.imagen
                    )
                }
            ) { innerPadding ->


                // Estado para controlar el desplazamiento de la lista
                val listState = rememberLazyListState()

                // Observa los mensajes del ViewModel
                val mensajes by viewModel.mensajes.collectAsState()

                // Efecto para desplazar la lista al final cuando cambie la lista de mensajes
                LaunchedEffect(mensajes) {
                    listState.scrollToItem(index = mensajes.size - 1)
                }

                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Bottom
                ){
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f)
                    ) {
                        items(mensajes) { mensaje ->
                            MensajeCard(mensaje)
                        }
                    }
                    CajaDeTexto(
                        enviarMensaje = {
                            GlobalScope.launch {
                                viewModel.enviarMensaje(it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }


            }

        }
        is ChatUiState.Error -> {
            ErrorScreen (
                navigateToStart = {
                    navigateToStart()
                }
            )
        }
    }

}

@Composable
fun MensajeCard(mensaje: Mensaje) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if(Sesion.usuario.id == mensaje.emisor!!.id) Arrangement.End else Arrangement.Start
    ) {
        Card (
            modifier = Modifier.padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = if(Sesion.usuario.id == mensaje.emisor.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary)
        ){
            Column (
                horizontalAlignment = if(Sesion.usuario.id == mensaje.emisor.id) Alignment.End else Alignment.Start,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            ){
                Text(text = mensaje.texto, textAlign = if(Sesion.usuario.id == mensaje.emisor.id) TextAlign.Right else TextAlign.Left)
                Text(text = mensaje.hora, style = MaterialTheme.typography.labelSmall)
            }
        }
    }

}

@Composable
fun CajaDeTexto(
    enviarMensaje: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var texto by remember { mutableStateOf("") }

    OutlinedTextField(
        value = texto,
        onValueChange = { texto = it },
        label = { Text("Escribe un mensaje...") },
        trailingIcon = {
            IconButton(onClick = {
                enviarMensaje(texto)
                texto = ""
            }) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions(onSend = {
            enviarMensaje(texto)
            texto = ""
        }),
        modifier = modifier
    )

}
