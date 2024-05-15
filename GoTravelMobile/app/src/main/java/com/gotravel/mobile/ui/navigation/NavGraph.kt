package com.gotravel.mobile.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gotravel.mobile.ui.screen.CambiarContrasenaDestination
import com.gotravel.mobile.ui.screen.CambiarContrasenaScreen
import com.gotravel.mobile.ui.screen.CrearViajeDestination
import com.gotravel.mobile.ui.screen.CrearViajeScreen
import com.gotravel.mobile.ui.screen.CredencialesDestination
import com.gotravel.mobile.ui.screen.CredencialesScreen
import com.gotravel.mobile.ui.screen.HomeDestination
import com.gotravel.mobile.ui.screen.HomeScreen
import com.gotravel.mobile.ui.screen.LandingDestination
import com.gotravel.mobile.ui.screen.LandingScreen
import com.gotravel.mobile.ui.screen.PerfilDestination
import com.gotravel.mobile.ui.screen.PerfilScreen
import com.gotravel.mobile.ui.screen.ViajesDestination
import com.gotravel.mobile.ui.screen.ViajesScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    NavHost (
        navController = navController,
        startDestination = LandingDestination.route,
        modifier = modifier
    ) {

        // Pantalla landing
        composable(route = LandingDestination.route) {
            LandingScreen(
                navigateToCredenciales = {
                    navController.navigate("${CredencialesDestination.route}/${it}")
                }
            )
        }

        // Pantalla para introducir los credenciales de inicio de sesión o registro
        composable(
            route = CredencialesDestination.routeWithArgs,
            arguments = listOf(
                navArgument(CredencialesDestination.opcion) { type = NavType.StringType },
            )) {
            CredencialesScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                navigateToHome = {
                    navController.navigate(HomeDestination.route)
                },
                navigateToCredenciales = {
                    navController.navigate("${CredencialesDestination.route}/${it}")
                }
            )
        }

        // Pantalla home
        composable(route = HomeDestination.route) {
            HomeScreen(
                navController = navController,
                onViajeClicked = {
                    //TODO
                },
                navigateToCrearViaje = {
                    navController.navigate(CrearViajeDestination.route)
                }
            )
        }

        // Pantalla viajes sin busqueda
        composable(route = ViajesDestination.route) {
            ViajesScreen(
                navController = navController,
                buscarViaje = { busqueda ->
                    navController.navigate(ViajesDestination.route + if(busqueda.isNotBlank()) "/${busqueda}" else "")
                },
                onViajeClicked = {
                    //TODO
                }
            )
        }

        // Pantalla viajes con busqueda
        composable(
            route = ViajesDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ViajesDestination.busqueda) { type = NavType.StringType },
            )) {
            ViajesScreen(
                navController = navController,
                buscarViaje = { busqueda ->
                    navController.navigate(ViajesDestination.route + if(busqueda.isNotBlank()) "/${busqueda}" else "")
                },
                onViajeClicked = {
                    //TODO
                }
            )
        }

        // Pantalla perfil
        composable(route = PerfilDestination.route) {
            PerfilScreen(
                navController = navController,
                navigateToCambiarContrasena = {
                    navController.navigate(CambiarContrasenaDestination.route)
                }
            )
        }

        // Pantalla para cambiar la contraseña
        composable(route = CambiarContrasenaDestination.route) {
            CambiarContrasenaScreen(
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }

        // Pantalla para crear un nuevo viaje
        composable(route = CrearViajeDestination.route) {
            CrearViajeScreen(
                navigateToViaje = { },
                navController = navController
            )
        }

    }

}