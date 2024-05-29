package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.Screen
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ServiciosUiState
import com.gotravel.mobile.ui.screen.viewmodels.ServiciosViewModel

object ServiciosDestination : NavDestination {
    override val route = "servicios"
    override val titleRes = R.string.app_name
    const val busqueda = "busqueda"
    val routeWithArgs = "$route/{$busqueda}"
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiciosScreen(
    viewModel: ServiciosViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    onServicioClicked: (Int) -> Unit,
    buscarServicio: (String) -> Unit,
    elementosDeNavegacion: List<Screen>,
    navigateToStart: () -> Unit
) {

    when (val uiState = viewModel.uiState) {
        is ServiciosUiState.Loading -> {
            AppLoadingScreen(
                navController = navController,
                elementosDeNavegacion = elementosDeNavegacion
            )
        }
        is ServiciosUiState.Success -> {
            ServiciosContent(
                navController = navController,
                serviciosPublicados = uiState.serviciosPublicados,
                serviciosOcultos = uiState.serviciosOcultos,
                buscarServicio = buscarServicio,
                onServicioClicked = onServicioClicked,
                elementosDeNavegacion = elementosDeNavegacion
            )
        }
        else -> ErrorScreen(navigateToStart = navigateToStart)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiciosContent(
    navController: NavHostController,
    serviciosPublicados: List<Servicio>,
    serviciosOcultos: List<Servicio>,
    buscarServicio: (String) -> Unit,
    onServicioClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    elementosDeNavegacion: List<Screen>
) {

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = ServiciosDestination.titleRes),
                canNavigateBack = false
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = ServiciosDestination.route,
                navController = navController,
                elementosDeNavegacion
            )
        },
        modifier = modifier
    ) {

        if(serviciosOcultos.isEmpty() && serviciosPublicados.isEmpty()) {
            SinServicios(
                modifier = Modifier.padding(it),
                color = MaterialTheme.colorScheme.onSurface,
                buscarServicio = buscarServicio
            )
        } else {
            GridServicios(
                serviciosPublicados = serviciosPublicados,
                serviciosOcultos = serviciosOcultos,
                buscarServicio = buscarServicio,
                modifier = Modifier.padding(it),
                onServicioClicked = onServicioClicked
            )
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GridServicios(
    serviciosPublicados: List<Servicio>,
    serviciosOcultos: List<Servicio>,
    buscarServicio: (String) -> Unit,
    onServicioClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
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
                buscarServicio(nombreServicio)
            }),
            buscar = buscarServicio,
            modifier = Modifier
                .padding(8.dp),
            label = "servicios"
        )
        
        val opciones = listOf("ocultos", "publicados")
        
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
                    Text(text = "OCULTOS", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = { eleccion = opciones[1] },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(
                        text = "PUBLICADOS",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.padding(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(4.dp)
            ) {
                items(items = if(eleccion == opciones[0]) serviciosOcultos else serviciosPublicados) {servicio ->
                    ServicioCard(
                        servicio = servicio,
                        onServicioClicked = onServicioClicked,
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
fun SinServicios(
    modifier: Modifier,
    color: Color,
    buscarServicio: (String) -> Unit
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
                buscarServicio(nombreServicio)
            }),
            buscar = buscarServicio,
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
            Text(text = "No tienes servicios", color = color)
        }
    }
    
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServicioCard(
    servicio: Servicio,
    onServicioClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
) {
    Card (
        modifier = modifier.clickable {
            onServicioClicked(servicio.id!!)
        },
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = elevation
    ){
        Row (
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){

            var textColor = MaterialTheme.colorScheme.onPrimary

            if(color == MaterialTheme.colorScheme.onPrimary) {
                textColor = MaterialTheme.colorScheme.primary
            }

            if(servicio.imagenes.isNotEmpty()) {
                Image(
                    bitmap = servicio.imagenes[0].foto,
                    contentDescription = "",
                    modifier = Modifier
                        .size(64.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            } else {
                Card (
                    modifier = Modifier
                        .size(64.dp)
                        .aspectRatio(1f),
                ){
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = ""
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))

            Column {
                Text(text = servicio.nombre, color = textColor)
                Text(text = servicio.inicio + " - " + servicio.final.ifBlank { servicio.hora }, fontSize = 8.sp, color = textColor)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "", tint = textColor)

        }
    }
}