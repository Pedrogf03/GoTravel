package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.BuscarServiciosUiState
import com.gotravel.mobile.ui.screen.viewmodels.BuscarServiciosViewModel

object BuscarServiciosDestination : NavDestination {
    override val route = "buscarServicios"
    override val titleRes = R.string.app_name
    const val idEtapa = "idEtapa"
    val routeWithArgs = "$route/{$idEtapa}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BuscarServiciosScreen(
    navigateUp: () -> Unit,
    navigateToStart: () -> Unit,
    viewModel: BuscarServiciosViewModel = viewModel(factory = AppViewModelProvider.Factory),
    buscarServicio: (String) -> Unit,
    onServicioClicked: (Int, Int) -> Unit,
) {

    when(val uiState = viewModel.uiState) {
        is BuscarServiciosUiState.Loading -> {
            LandingLoadingScreen()
        }
        is BuscarServiciosUiState.Success -> {

            Scaffold (
                topBar = {
                    AppTopBar(
                        title = stringResource(id = ViajeDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navigateUp() }
                    )
                }
            ){ innerPadding ->

                if(uiState.servicios.isEmpty()) {
                    SinServicios(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.onSurface,
                        buscarServicio = buscarServicio
                    )
                } else {
                    GridBuscarServicios(
                        servicios = uiState.servicios,
                        buscarServicio = buscarServicio,
                        onServicioClicked = {
                            onServicioClicked(it, viewModel.idEtapa)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

            }

        }
        else -> ErrorScreen { navigateToStart() }
    }



}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GridBuscarServicios(
    servicios: List<Servicio>,
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

        Card (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ){

            LazyColumn(
                contentPadding = PaddingValues(4.dp)
            ) {
                items(items = servicios) {servicio ->
                    ServicioCard(
                        servicio = servicio,
                        onServicioClicked = onServicioClicked,
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    )
                }
            }

        }

    }

}