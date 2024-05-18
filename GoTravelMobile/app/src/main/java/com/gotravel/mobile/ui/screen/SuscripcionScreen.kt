package com.gotravel.mobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.SuscripcionViewModel

object SuscripcionDestination : NavDestination {
    override val route = "suscripcion"
    override val titleRes = R.string.app_name
    const val esProfesional = "esProfesional"
    val routeWithArgs = "$route/{${esProfesional}}"
}

@Composable
fun SuscripcionScreen (
    modifier: Modifier = Modifier,
    viewModel: SuscripcionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
){

    val esProfesional = viewModel.esProfesional

    Scaffold (
        topBar = {
            AppTopBar(
                title = stringResource(id = CrearViajeDestination.titleRes),
                canNavigateBack = true,
                navigateUp = { navController.popBackStack() }
            )
        },
        modifier = modifier
    ){ innerPadding ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if(esProfesional) {
                // TODO
            } else {
                SuscribirseScreen(viewModel)
            }
        }

    }

}

@Composable
fun SuscribirseScreen(
    viewModel: SuscripcionViewModel
) {

    Card (
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido al Programa para Profesionales de GoTravel!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Tienes un servicio que puede mejorar la experiencia de viaje de nuestros usuarios? " +
                        "¡Estás en el lugar correcto! Nuestro programa para profesionales está abierto a todo " +
                        "aquel que desee compartir sus recursos para que otros usuarios puedan disfrutar de ellos.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Qué puedes ofrecer?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Desde alojamiento, alquiler de vehículos, guías turísticas, hasta la oportunidad de compartir " +
                        "un viaje, ¡las posibilidades son infinitas! Si tienes un servicio que puede mejorar la experiencia" +
                        " de viaje de nuestros usuarios, queremos saber de ti.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Cómo funciona?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Por una suscripción mensual de solo 4.99€, puedes convertirte en un proveedor de servicios en nuestra plataforma. " +
                        "Esto te permitirá publicar y promocionar tus servicios a nuestra creciente comunidad de viajeros.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Por qué unirse?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Al unirte a nuestro programa para profesionales, no solo tendrás la oportunidad de aumentar tus ingresos, sino que también " +
                        "podrás recibir valoraciones de los clientes y mejorar continuamente tus servicios basándote en sus comentarios.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¡Únete a nosotros hoy y comienza tu viaje hacia el éxito!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = "Unirse",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

}
