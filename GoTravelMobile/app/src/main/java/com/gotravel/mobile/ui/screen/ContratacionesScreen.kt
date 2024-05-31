package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ContratacionesUiState
import com.gotravel.mobile.ui.screen.viewmodels.ContratacionesViewModel
import com.gotravel.mobile.ui.screen.viewmodels.CrearServicioUiState

object ContratacionesDestination : NavDestination {
    override val route = "contrataciones"
    override val titleRes = R.string.app_name
    const val busqueda = "busqueda"
    val routeWithArgs = "$route/{$busqueda}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContratacionesScreen (
    navigateToServicio: (Int) -> Unit,
    buscarServicio: (String) -> Unit,
    viewModel: ContratacionesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
) {

    when (val uiState = viewModel.uiState) {
        is ContratacionesUiState.Loading -> {
            LandingLoadingScreen()
        }
        is ContratacionesUiState.Success -> {
            Scaffold (
                topBar = {
                    AppTopBar(
                        title = stringResource(id = CrearViajeDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navController.popBackStack() }
                    )
                }
            ){innerPadding ->

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
                            navigateToServicio(it)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

            }
        }
        is ContratacionesUiState.Error -> {
            ErrorScreen(
                navigateToStart = {
                    navController.navigate(LandingDestination.route)
                }
            )
        }
    }

}