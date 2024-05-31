package com.gotravel.mobile.ui.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Rol
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.Screen
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.PerfilViewModel
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object PerfilDestination : NavDestination {
    override val route = "perfil"
    override val titleRes = R.string.app_name
}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigateToEditarPerfil: () -> Unit,
    navigateToContrataciones: () -> Unit,
    navigateToSuscripcion: (Boolean) -> Unit,
    navigateToStart: () -> Unit,
    elementosDeNavegacion: List<Screen>,
    viewModel: PerfilViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = PerfilDestination.titleRes),
                canNavigateBack = false
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = PerfilDestination.route,
                navController = navController,
                items = elementosDeNavegacion
            )
        },
        modifier = modifier
    ) { innerPadding ->

        Column (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){

            InformacionUsuario(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.25f)
            )

            PerfilScreenContent(modifier = Modifier
                .fillMaxSize()
                .weight(0.75f),
                esProfesional = Sesion.usuario.roles.contains(Rol("Profesional")),
                navigateToEditarPerfil = navigateToEditarPerfil,
                navigateToContrataciones = navigateToContrataciones,
                navigateToSuscripcion = navigateToSuscripcion,
                cerrarSesion = {
                    GlobalScope.launch() {
                        viewModel.cerrarSesion()
                    }
                },
                navigateToStart = navigateToStart,
                cerrarServidor = {
                    viewModel.cerrarServidor()
                }
            )

        }

    }

}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun PerfilScreenContent(
    modifier: Modifier = Modifier,
    esProfesional: Boolean,
    navigateToEditarPerfil: () -> Unit,
    navigateToContrataciones: () -> Unit,
    navigateToSuscripcion: (Boolean) -> Unit,
    cerrarSesion: () -> Unit,
    navigateToStart: () -> Unit,
    cerrarServidor: () -> Unit
) {
    Card (
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = { navigateToEditarPerfil() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = "EDITAR PERFIL",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = { navigateToContrataciones() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "SERVICIOS CONTRATADOS",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = { navigateToSuscripcion(esProfesional) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = if(esProfesional) "MI SUSCRIPCIÓN" else "PROGRAMA DE PROFESIONALES",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            val context = LocalContext.current
            Button(
                onClick = {
                    cerrarSesion()
                    val sharedPref = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
                    with (sharedPref.edit()) {
                        putString("email", "")
                        putString("contraseña", "")
                        apply()
                    }
                    navigateToStart()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "CERRAR SESIÓN",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            if(Sesion.usuario.roles.contains(Rol("Administrador"))) {
                Button(
                    onClick = {
                        GlobalScope.launch {
                            cerrarSesion()
                            cerrarServidor()
                        }
                        navigateToStart()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(
                        text = "CERRAR SERVIDOR",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card (
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "")
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "¡Y recuerda llevar tu documentación siempre encima al viajar!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }

    }
}