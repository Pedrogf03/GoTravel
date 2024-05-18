package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Etapa
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ViajeUiState
import com.gotravel.mobile.ui.screen.viewmodels.ViajeViewModel

object ViajeDestination : NavDestination {
    override val route = "viaje"
    override val titleRes = R.string.app_name
    const val idViaje = "idViaje"
    val routeWithArgs = "$route/{$idViaje}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajeScreen(
    viewModel: ViajeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateUp: () -> Unit
) {

    val retryAction = viewModel::getAllFromViaje

    when (val uiState = viewModel.uiState) {
        is ViajeUiState.Loading -> {
            LandingLoadingScreen()
        }
        is ViajeUiState.Success -> {

            Scaffold (
                topBar = {
                    AppTopBar(
                        title = stringResource(id = ViajeDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navigateUp() }
                    )
                }
            ){

                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    InformacionViaje(
                        viaje = uiState.viaje,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.25f)
                            .padding(8.dp)
                    )
                    MostrarEtapas(
                        etapas = uiState.viaje.etapas,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.75f)
                    )
                }

            }

        }
        else -> ErrorScreen(retryAction = retryAction)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InformacionViaje(
    modifier: Modifier = Modifier,
    viaje: Viaje
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        Column (
            modifier = Modifier
                .fillMaxSize()
                .weight(0.75f),
            verticalArrangement = Arrangement.Center
        ){

            Text(text = viaje.nombre)

            Spacer(modifier = Modifier.padding(8.dp))

            if(viaje.descripcion != null) {
                Text(text = viaje.descripcion)

                Spacer(modifier = Modifier.padding(8.dp))
            }

            Text(text = "${viaje.inicio} - ${viaje.final}")

            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Precio total: ${viaje.costeTotal}€")

        }

        Row (
            modifier = Modifier
                .fillMaxSize()
                .weight(0.25f),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End
        ){
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = ""
                )
            }
        }

    }
}

@Composable
fun MostrarEtapas(
    modifier: Modifier = Modifier,
    etapas: List<Etapa>
) {
    Card (
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "NUEVA ETAPA", style = MaterialTheme.typography.titleMedium)
                }
            }

            if(etapas.isEmpty()) {
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
                    Text(text = "Este viaje aún no tiene etapas", color = MaterialTheme.colorScheme.onSurface)
                }
            }

        }
    }
}