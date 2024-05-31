package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Mensaje
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.Screen
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ChatsUiState
import com.gotravel.mobile.ui.screen.viewmodels.ChatsViewModel
import com.gotravel.mobile.ui.utils.Sesion

object ChatsDestination : NavDestination {
    override val route = "chats"
    override val titleRes = R.string.app_name
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatsScreen (
    navigateToChat: (Int) -> Unit,
    viewModel: ChatsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    elementosDeNavegacion: List<Screen>,
) {

    when(val uiState = viewModel.uiState) {
        is ChatsUiState.Loading -> {
            AppLoadingScreen(navController = navController, elementosDeNavegacion = elementosDeNavegacion)
        }
        is ChatsUiState.Success -> {

            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(id = ChatsDestination.titleRes),
                        canNavigateBack = false
                    )
                },
                bottomBar = {
                    AppBottomBar(
                        currentRoute = ChatsDestination.route,
                        navController = navController,
                        items = elementosDeNavegacion
                    )
                }
            ) { innerPadding ->

                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(8.dp)
                ){
                    if(uiState.conversaciones.isEmpty()) {
                        SinConversaciones()
                    } else {
                        LazyColumn {
                            items(uiState.conversaciones.entries.toList()) { conversacion ->
                                ConversacionCard(
                                    conversacion = conversacion,
                                    navigateToChat = {
                                        navigateToChat(it)
                                    }
                                )
                            }
                        }
                    }
                }

            }

        }
        is ChatsUiState.Error -> {
            ErrorScreen(navigateToStart = {
                navController.navigate(LandingDestination.route)
            })
        }
    }

}

@Composable
fun ConversacionCard(
    conversacion: Map.Entry<Int, Mensaje>,
    navigateToChat: (Int) -> Unit
) {

    val foto = if(conversacion.value.emisor!!.id == Sesion.usuario.id) conversacion.value.receptor!!.imagen else conversacion.value.emisor!!.imagen
    val nombre = if(conversacion.value.emisor!!.id == Sesion.usuario.id) conversacion.value.receptor!!.nombre else conversacion.value.emisor!!.nombre

    println(foto)

    Card (
        modifier = Modifier
            .clickable {
                navigateToChat(conversacion.key)
            }
            .padding(8.dp),
        shape = RoundedCornerShape(50)
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            if(foto != null){
                Image(
                    bitmap = foto,
                    contentDescription = "",
                    modifier = Modifier
                        .size(75.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.usernofoto),
                    contentDescription = "",
                    modifier = Modifier
                        .size(75.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))
            
            Column (
                modifier = Modifier.fillMaxWidth()
            ){
                Text(text = nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = conversacion.value.texto, style = MaterialTheme.typography.labelMedium)
            }
            
        }
    }

}

@Composable
fun SinConversaciones(
    modifier: Modifier = Modifier
) {

    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Column (
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(text = "No tienes conversaciones", color = MaterialTheme.colorScheme.onSurface)
        }
    }

}