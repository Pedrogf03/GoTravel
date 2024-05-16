package com.gotravel.clienteMovil.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.clienteMovil.R
import com.gotravel.clienteMovil.data.Usuario
import com.gotravel.clienteMovil.data.Viaje
import com.gotravel.clienteMovil.ui.AppViewModelProvider
import com.gotravel.clienteMovil.ui.GoTravelBottomBar
import com.gotravel.clienteMovil.ui.GoTravelTopAppBar
import com.gotravel.clienteMovil.ui.navigation.NavigationDestination

object ViajesDestination : NavigationDestination {
    override val route = "viajes"
    override val titleRes = R.string.tus_viajes
    const val usuarioId = "usuarioId"
    const val busqueda = "busqueda"
    val routeWith1Arg = "$route/{$usuarioId}/"
    val routeWith2Args = "$route/{$usuarioId}/{$busqueda}"
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajesScreen (
    modifier: Modifier = Modifier,
    viewModel: ViajesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    buscarViaje: (Usuario, String) -> Unit,
    navigateToNuevoViaje: (Usuario) -> Unit
) {
    val viajesUiState = viewModel.viajesUiState
    val retryAction = viewModel::findUsuarioById

    when (viajesUiState) {
        is ViajesUiState.Loading -> {
            LoadingScreen(
                modifier = modifier
                    .fillMaxSize()
            )
        }
        is ViajesUiState.Success -> {

            Scaffold(
                topBar = {
                    GoTravelTopAppBar(
                        title = stringResource(ViajesDestination.titleRes),
                        canNavigateBack = false
                    )
                },
                bottomBar = {
                    GoTravelBottomBar(
                        currentRoute = ViajesDestination.route,
                        navController = navController,
                        usuarioId = viajesUiState.usuario.id
                    )
                }
                , modifier = modifier
            ) { innerPadding ->

                var busqueda by remember{ mutableStateOf("") }

                Column (modifier = Modifier.padding(innerPadding)){
                    SearchEngine(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = {
                            buscarViaje(viajesUiState.usuario, busqueda)
                        }),
                        buscarViaje = buscarViaje,
                        usuario = viajesUiState.usuario
                    )

                    Row(
                        modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Button(onClick = { navigateToNuevoViaje(viajesUiState.usuario )}, modifier.fillMaxWidth()) {
                            Text(text = "Nuevo viaje", modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                    }
                    
                    ViajesColumn(
                        viajes = viajesUiState.viajes,
                        onViajeClicked = { TODO("Navegar to viaje") }
                    )
                    
                }

            }

        }
        else -> {
            ErrorScreen(
                retryAction = retryAction
            )
        }

    }

}

@Composable
fun SearchEngine(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    buscarViaje: (Usuario, String) -> Unit,
    modifier: Modifier = Modifier,
    usuario: Usuario
){
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.busca_tu_viaje)) },
        leadingIcon = {
            IconButton(onClick = { buscarViaje(usuario, value) }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajesColumn(
    modifier: Modifier = Modifier,
    viajes: List<Viaje>,
    onViajeClicked: (Int) -> Unit
) {

    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    var x by remember { mutableIntStateOf(1) }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
        ),
        exit = fadeOut(),
        modifier = Modifier
    ) {

        LazyColumn(
            modifier.fillMaxSize()
        ) {
            items(items = viajes) { viaje ->
                if(x == 14) { // Esta variable es para que al deslizar verticalmente sobre la pantalla de todos los juegos, la animacion no se vuelva exagerada. Cuando se muestran 14 juegos, el valor se reinicia.
                    x = 1
                }
                ViajeCard(
                    viaje = viaje,
                    onViajeClicked = onViajeClicked
                    )
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajeCard(
    viaje: Viaje,
    onViajeClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ){
            Text(text = viaje.nombre)
            if(!viaje.descripcion.isNullOrBlank()) {
                Text(text = viaje.descripcion)
            }
            Text(text = "Inicio " + viaje.fechaInicio)
            if(!viaje.fechaFin.isNullOrBlank()){
                Text(text = "Final " + viaje.fechaFin)
            }
            Text(text = "Coste total del viaje: " + viaje.costeTotal + "€")
        }
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_signal_wifi_connected_no_internet_4_24), contentDescription = ""
        )
        Text(text = stringResource(R.string.no_se_ha_podido_conectar_con_el_servidor), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(text = stringResource(R.string.reintentar))
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.cargando)
    )
}