package com.gotravel.mobile.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
            LoadingScreen()
        }
        is LandingUiState.Success -> {
            val imagen = ImageResource.Bitmap(uiState.imagen)
            val context = LocalContext.current
            ContentLanding(imagen = imagen, navigateToCredenciales, cambiarIp = { viewModel.cambiarIp(context, it) })
        }
        is LandingUiState.Error -> {
            val imagen = ImageResource.Res(uiState.imagen)
            val context = LocalContext.current
            ContentLanding(imagen = imagen, navigateToCredenciales, cambiarIp = { viewModel.cambiarIp(context, it) })
        }
    }

}

sealed class ImageResource {
    data class Res(val id: Int) : ImageResource()
    data class Bitmap(val bitmap: ImageBitmap) : ImageResource()
}

@Composable
fun ContentLanding(
    imagen: ImageResource,
    navigateToCredenciales: (String) -> Unit,
    cambiarIp: (String) -> Unit
) {

    var dialogAbierto by remember { mutableStateOf(false) }

    if (dialogAbierto) {
        Dialog(onDismissRequest = { dialogAbierto = false }) {
            CambiarDirIp(cambiarIp) { dialogAbierto = !dialogAbierto }
        }
    }

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
                when (imagen) {
                    is ImageResource.Res -> {
                        Image(
                            painter = painterResource(id = imagen.id),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    is ImageResource.Bitmap -> {
                        Image(
                            bitmap = imagen.bitmap,
                            contentDescription = "Imagen aleatoria de una ciudad",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(150.dp)
                        .clip(CircleShape)
                        .clickable {
                            dialogAbierto = !dialogAbierto
                        }
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

@Composable
fun CambiarDirIp(
    cambiarIp: (String) -> Unit,
    cerrarDialogo: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        var ip by remember { mutableStateOf("") }

        Card{
            Column(
                modifier = Modifier.wrapContentSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("Direccion IP del servidor") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = ""
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Button(
                    onClick = {
                        cambiarIp(ip)
                        cerrarDialogo()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cambiar")
                }

            }

        }

    }
}