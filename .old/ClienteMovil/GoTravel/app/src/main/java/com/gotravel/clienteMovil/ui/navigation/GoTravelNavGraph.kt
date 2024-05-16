package com.gotravel.clienteMovil.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gotravel.clienteMovil.ui.screens.LoginDestination
import com.gotravel.clienteMovil.ui.screens.LoginScreen
import com.gotravel.clienteMovil.ui.screens.NuevoViajeDestination
import com.gotravel.clienteMovil.ui.screens.NuevoViajeScreen
import com.gotravel.clienteMovil.ui.screens.ViajesDestination
import com.gotravel.clienteMovil.ui.screens.ViajesScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoTravelNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    windowSize: WindowWidthSizeClass
) {

    NavHost (
        navController = navController,
        startDestination = LoginDestination.route,
        modifier = modifier
    ) {

        // Pantalla login
        composable(route = LoginDestination.route) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate("${ViajesDestination.route}/${it}/")
                }
            )
        }

        // Pantalla viajes sin busqueda
        composable(
            route = ViajesDestination.routeWith1Arg,
            arguments = listOf(
                navArgument(ViajesDestination.usuarioId) { type = NavType.IntType },
            )) {
            ViajesScreen(
                navController = navController,
                buscarViaje = { usuario, busqueda ->
                    navController.navigate("${ViajesDestination.route}/${usuario.id}/${busqueda}")
                },
                navigateToNuevoViaje = {
                    navController.navigate("${NuevoViajeDestination.route}/${it.id}")
                }
            )
        }

        // Pantalla viajes con busqueda
        composable(
            route = ViajesDestination.routeWith2Args,
            arguments = listOf(
                navArgument(ViajesDestination.usuarioId) { type = NavType.IntType },
                navArgument(ViajesDestination.busqueda) { type = NavType.StringType }
            )) {
            ViajesScreen(
                navController = navController,
                buscarViaje = { usuario, busqueda ->
                    navController.navigate("${ViajesDestination.route}/${usuario.id}/${busqueda}")
                },
                navigateToNuevoViaje = {
                    navController.navigate("${NuevoViajeDestination.route}/${it.id}")
                }
            )
        }

        // Pantalla de nuevo viaje
        composable(
            route = NuevoViajeDestination.routeWithArgs,
            arguments = listOf(
                navArgument(NuevoViajeDestination.usuarioId) { type = NavType.IntType }
            )) {
            NuevoViajeScreen(
                navController = navController,
                navigateToHome = {
                    navController.navigate("${ViajesDestination.route}/${it}/")
                }
            )
        }

    }

}

