package com.gotravel.mobile.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodel.ViajesUiState
import com.gotravel.mobile.ui.screen.viewmodel.ViajesViewModel

object ViajesDestination : NavDestination {
    override val route = "viajes"
    override val titleRes = R.string.tus_viajes
}
@Composable
fun ViajesScreen(
    viewModel: ViajesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
) {

    val retryAction = viewModel::getViajes

    when (val uiState = viewModel.uiState) {
        is ViajesUiState.Loading -> {
            LoadingScreen()
        }
        is ViajesUiState.Success -> {
            ViajesContent(navController, uiState.viajes)
        }
        else -> ErrorScreen(retryAction = retryAction)
    }

}

@Composable
fun ViajesContent(
    navController: NavHostController,
    viajes: List<Viaje>,
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

        Text(text = viajes.size.toString(), modifier = Modifier.padding(it))

    }

}
