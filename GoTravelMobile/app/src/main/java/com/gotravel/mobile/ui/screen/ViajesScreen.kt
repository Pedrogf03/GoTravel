package com.gotravel.mobile.ui.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
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
    viewModel: ViajesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    when (val uiState = viewModel.uiState) {
        is ViajesUiState.Loading -> {
            LoadingScreen()
        }
        is ViajesUiState.Success -> {
            println(uiState.viajes)
        }
        else -> TODO()
    }

}
