package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
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
import com.gotravel.mobile.ui.Screen
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ViajesUiState
import com.gotravel.mobile.ui.screen.viewmodels.ViajesViewModel

object ViajesDestination : NavDestination {
    override val route = "viajes"
    override val titleRes = R.string.app_name
    const val busqueda = "busqueda"
    val routeWithArgs = "$route/{$busqueda}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajesScreen(
    viewModel: ViajesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    onViajeClicked: (Int) -> Unit,
    buscarViaje: (String) -> Unit,
    elementosDeNavegacion: List<Screen>,
    navigateToStart: () -> Unit
) {

    when (val uiState = viewModel.uiState) {
        is ViajesUiState.Loading -> {
            AppLoadingScreen(
                navController = navController,
                elementosDeNavegacion = elementosDeNavegacion
            )
        }
        is ViajesUiState.Success -> {
            ViajesContent(
                navController = navController,
                viajes = uiState.viajes,
                viajesPasados = uiState.viajesPasados,
                buscarViaje = buscarViaje,
                onViajeClicked = onViajeClicked,
                elementosDeNavegacion = elementosDeNavegacion
            )
        }
        else -> ErrorScreen(navigateToStart = navigateToStart)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajesContent(
    navController: NavHostController,
    viajes: List<Viaje>,
    viajesPasados: List<Viaje>,
    buscarViaje: (String) -> Unit,
    onViajeClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    elementosDeNavegacion: List<Screen>
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
                navController = navController,
                elementosDeNavegacion
            )
        },
        modifier = modifier
    ) {

        if(viajesPasados.isEmpty() && viajes.isEmpty()) {
            SinViajes(
                modifier = Modifier.padding(it),
                color = MaterialTheme.colorScheme.onSurface,
                buscarViaje = buscarViaje
            )
        } else {
            GridViajes(
                viajes = viajes,
                viajesPasados = viajesPasados,
                buscarViaje = buscarViaje,
                modifier = Modifier.padding(it),
                onViajeClicked = onViajeClicked
            )
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GridViajes(
    viajes: List<Viaje>,
    viajesPasados: List<Viaje>,
    buscarViaje: (String) -> Unit,
    onViajeClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Column (
        modifier = modifier
            .fillMaxSize(),
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
            buscar = buscarViaje,
            modifier = Modifier
                .padding(8.dp),
            label = "viajes"
        )
        
        val opciones = listOf("finalizados", "actuales")
        
        var eleccion by remember { mutableStateOf(opciones[1]) }

        Card (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
            colors = CardDefaults.cardColors(containerColor = if(eleccion == opciones[0]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary)
        ){
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = { eleccion = opciones[0] },
                ) {
                    Text(text = "FINALIZADOS", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = { eleccion = opciones[1] },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(
                        text = "ACTUALES",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.padding(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(4.dp)
            ) {
                items(items = if(eleccion == opciones[0]) viajesPasados else viajes) {viaje ->
                    ViajeCard(
                        viaje = viaje,
                        onViajeClicked = onViajeClicked,
                        modifier = Modifier.padding(4.dp),
                        color = if(eleccion == opciones[0]) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    )
                }
            }
            
        }

    }

}

@Composable
fun SinViajes(
    modifier: Modifier,
    color: Color,
    buscarViaje: (String) -> Unit
) {

    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        var nombreServicio by remember { mutableStateOf("") }

        SearchEngine(
            value = nombreServicio,
            onValueChange = { nombreServicio = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = {
                buscarViaje(nombreServicio)
            }),
            buscar = buscarViaje,
            modifier = Modifier
                .padding(8.dp),
            label = "servicios"
        )

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
    
}

@Composable
fun SearchEngine(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    buscar: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Busca $label por su nombre") },
        trailingIcon = {
            IconButton(onClick = { buscar(value) }) {
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
    onViajeClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
) {
    Card (
        modifier = modifier.clickable {
            onViajeClicked(viaje.id!!)
        },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = elevation
    ){
        Row (
            modifier = Modifier
                .padding(8.dp)
                ,
            verticalAlignment = Alignment.CenterVertically
        ){

            var textColor = MaterialTheme.colorScheme.onPrimary

            if(color == MaterialTheme.colorScheme.onPrimary) {
                textColor = MaterialTheme.colorScheme.primary
            }

            Column {
                Text(text = viaje.nombre, color = textColor, style = MaterialTheme.typography.titleMedium)
                viaje.descripcion?.let { Text(text = it.take(50) + if(it.length > 50) "..." else "", fontSize = 12.sp, color = textColor)}
                Text(text = viaje.inicio + " - " + viaje.final, fontSize = 12.sp, color = textColor)
                Text(text = "Etapas: " + viaje.etapas.size, fontSize = 12.sp, color = textColor)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "ver viaje", tint = textColor)

        }
    }
}