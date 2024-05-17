package com.gotravel.mobile.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ViajeUiState
import com.gotravel.mobile.ui.screen.viewmodels.ViajeViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ViajesUiState

object ViajeDestination : NavDestination {
    override val route = "viaje"
    override val titleRes = R.string.app_name
    const val idViaje = "idViaje"
    val routeWithArgs = "$route/{$idViaje}"
}

@Composable
fun ViajeScreen(
    viewModel: ViajeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val retryAction = viewModel::getAllFromViaje

    when (val uiState = viewModel.uiState) {
        is ViajeUiState.Loading -> {
            LandingLoadingScreen()
        }
        is ViajeUiState.Success -> {

            Text(text = "hey")

        }
        else -> ErrorScreen(retryAction = retryAction)
    }

}