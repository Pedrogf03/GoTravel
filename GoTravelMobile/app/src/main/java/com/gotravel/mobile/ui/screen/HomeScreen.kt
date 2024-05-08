package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Rol
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.AppUiState
import com.gotravel.mobile.ui.screen.viewmodels.HomeUiState
import com.gotravel.mobile.ui.screen.viewmodels.HomeViewModel

object HomeDestination : NavDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    onViajeClicked: (Int) -> Unit
) {

    val retryAction = viewModel::getContent

    when (val uiState = viewModel.uiState) {
        is HomeUiState.Loading -> {
            AppLoadingScreen(navController = navController)
        }
        is HomeUiState.Success -> {

            val esProfesional = AppUiState.usuario.roles.contains(Rol("Profesional"))

            Scaffold(
                topBar = {
                    AppTopBar(
                        title = stringResource(id = HomeDestination.titleRes),
                        canNavigateBack = false
                    )
                },
                bottomBar = {
                    AppBottomBar(
                        currentRoute = HomeDestination.route,
                        navController = navController
                    )
                },
                modifier = modifier
            ) {

                Column (
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ){

                    InformacionUsuario(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.25f)
                    )

                    HomeScreenContent(modifier = Modifier
                        .fillMaxSize()
                        .weight(0.75f),
                        esProfesional = esProfesional,
                        proximoViaje = uiState.proximoViaje,
                        viajeActual = uiState.viajeActual,
                        imagen = uiState.imagen1,
                        onViajeClicked = onViajeClicked
                    )


                }

            }

        }
        else -> ErrorScreen(retryAction = retryAction)
    }

}

@Composable
fun InformacionUsuario(
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ){

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .weight(0.75f),
                verticalArrangement = Arrangement.Center
            ) {

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(imageVector = Icons.Default.Person, contentDescription = "")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(text = AppUiState.usuario.nombre, fontSize = 24.sp, modifier = Modifier.height(28.dp))
                        if(AppUiState.usuario.apellidos != null) {
                            Text(text = AppUiState.usuario.apellidos!!)
                        }
                    }
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(imageVector = Icons.Default.Email, contentDescription = "")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = AppUiState.usuario.email)
                }


                if(AppUiState.usuario.tfno != null) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "")
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = AppUiState.usuario.tfno!!)
                    }
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(imageVector = Icons.Default.Info, contentDescription = "")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = AppUiState.usuario.roles[0].nombre)
                }

            }
            Column (modifier = Modifier
                .fillMaxSize()
                .weight(0.25f),
                verticalArrangement = Arrangement.Center
            ){
                if(AppUiState.usuario.foto != null) {
                    Image(
                        bitmap = AppUiState.usuario.imagen,
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painterResource(id = R.drawable.usernofoto),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                    )
                }

            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    esProfesional: Boolean,
    proximoViaje: Viaje?,
    viajeActual: Viaje?,
    imagen: ImageBitmap,
    onViajeClicked: (Int) -> Unit
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
                    onClick = { TODO() },
                ) {
                    Text(text = "NUEVO VIAJE", style = MaterialTheme.typography.titleMedium)
                }
                if(esProfesional) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(
                        onClick = { TODO() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Text(
                            text = "NUEVO SERVICIO",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Image(
                bitmap = imagen,
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )
            
            Card (
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ){
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ){
                    if(viajeActual != null) {
                        Text(text = "Viaje en curso", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        ViajeCard(
                            viaje = viajeActual,
                            onViajeClicked = onViajeClicked
                        )
                    } else if (proximoViaje != null) {
                        Text(text = "Proximo viaje", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        ViajeCard(
                            viaje = proximoViaje,
                            onViajeClicked = onViajeClicked
                        )
                    } else {
                        Text(text = "No tienes planeado ningún viaje", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            }

        }
    }
}


