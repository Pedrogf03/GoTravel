package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ViajesUiState
import com.gotravel.mobile.ui.screen.viewmodels.ViajesViewModel

object ViajesDestination : NavDestination {
    override val route = "viajes"
    override val titleRes = R.string.tus_viajes
    const val busqueda = "busqueda"
    val routeWithArgs = "$route/{$busqueda}"
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajesScreen(
    viewModel: ViajesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    onViajeClicked: (Int) -> Unit,
    buscarViaje: (String) -> Unit
) {

    val retryAction = viewModel::getViajes

    when (val uiState = viewModel.uiState) {
        is ViajesUiState.Loading -> {
            AppLoadingScreen(navController = navController)
        }
        is ViajesUiState.Success -> {
            ViajesContent(
                navController,
                uiState.viajes,
                buscarViaje = buscarViaje,
                onViajeClicked = onViajeClicked
            )
        }
        else -> ErrorScreen(retryAction = retryAction)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajesContent(
    navController: NavHostController,
    viajes: List<Viaje>,
    buscarViaje: (String) -> Unit,
    onViajeClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = ViajesDestination.titleRes),
                canNavigateBack = false
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = ViajesDestination.route,
                navController = navController
            )
        },
        modifier = modifier
    ) {

        if(viajes.isEmpty()) {
            SinViajes(
                modifier = Modifier.padding(it),
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            ListaViajes(
                viajes = viajes,
                buscarViaje = buscarViaje,
                modifier = Modifier.padding(it),
                onViajeClicked = onViajeClicked
            )
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListaViajes(
    viajes: List<Viaje>,
    buscarViaje: (String) -> Unit,
    onViajeClicked: (Int) -> Unit,
    modifier: Modifier
) {

    Column (
        modifier = modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){

        var nombreViaje by remember { mutableStateOf("") }

        SearchEngine(
            value = nombreViaje,
            onValueChange = { nombreViaje = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                buscarViaje(nombreViaje)
            }),
            buscarViaje = buscarViaje
        )

        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(items = viajes) {viaje ->
                ViajeCard(
                    viaje = viaje,
                    onViajeClicked = onViajeClicked
                )
            }
        }


    }

}

@Composable
fun SinViajes(
    modifier: Modifier, color: Color
) {

    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "",
            tint = color
        )
        Text(text = "No tienes viajes", color = color)
    }
    
}

@Composable
fun SearchEngine(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    buscarViaje: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Busca un viaje por su nombre") },
        trailingIcon = {
            IconButton(onClick = { buscarViaje(value) }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
            .fillMaxWidth()
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajeCard(
    viaje: Viaje,
    onViajeClicked: (Int) -> Unit
) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onViajeClicked(viaje.id!!) },
            verticalAlignment = Alignment.CenterVertically
        ){

            Column {
                Text(text = viaje.nombre)
                Text(text = viaje.inicio + if(viaje.final != null) " - " + viaje.final else "", fontSize = 8.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "ver viaje")

        }
    }
}