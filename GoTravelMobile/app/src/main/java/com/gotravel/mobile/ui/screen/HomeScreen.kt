package com.gotravel.mobile.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Rol
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodel.AppUiState
import com.gotravel.mobile.ui.screen.viewmodel.HomeViewModel

object HomeDestination : NavDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
) {

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

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.25f)
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
                        Text(text = AppUiState.usuario.nombre, fontSize = 24.sp)
                        if(AppUiState.usuario.apellidos != null) {
                            Text(text = AppUiState.usuario.apellidos!!)
                        }
                        Text(text = AppUiState.usuario.email)
                        if(AppUiState.usuario.tfno != null) {
                            Text(text = AppUiState.usuario.tfno!!)
                        }
                        Text(text = AppUiState.usuario.roles[0].nombre)
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
                                    .clip(shape = RoundedCornerShape(0.dp))
                            )
                        } else {
                            Image(
                                painterResource(id = R.drawable.usernofoto),
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(0.dp))
                            )
                        }

                    }
                }

            }

            Card (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.75f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ){
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
                }
            }


        }

    }

}
