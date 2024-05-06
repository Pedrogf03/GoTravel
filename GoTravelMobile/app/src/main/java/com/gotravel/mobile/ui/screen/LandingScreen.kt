package com.gotravel.mobile.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodel.LandingUiState
import com.gotravel.mobile.ui.screen.viewmodel.LandingViewModel

object LandingDestination : NavDestination {
    override val route = "landing"
    override val titleRes = R.string.app_name
}

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToCredenciales: (String) -> Unit
) {

    when (val uiState = viewModel.uiState) {
        is LandingUiState.Loading -> {
            LoadingScreen(
                modifier = modifier
                    .fillMaxSize()
            )
        }
        is LandingUiState.Success -> {
            ContentLanding(imagen = uiState.imagen, navigateToCredenciales)
        }
        is LandingUiState.Error -> {
            LoadingScreen(
                modifier = modifier
                    .fillMaxSize()
            )
        }
    }

}

@Composable
fun ContentLanding(
    imagen: ImageBitmap,
    navigateToCredenciales: (String) -> Unit
) {

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){

        Row (
            modifier = Modifier
                .weight(0.75f)
                .fillMaxWidth()
        ){
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Imagen de fondo
                Image(
                    bitmap = imagen,
                    contentDescription = "Imagen aleatoria de una ciudad",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Imagen del medio",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(150.dp) // Cambia esto para ajustar el tamaño
                        .clip(CircleShape) // Esto hace que la imagen sea redonda
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxSize()
                .padding(36.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navigateToCredenciales("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navigateToCredenciales("registro") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = "Registrarse",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

    }


}