package com.gotravel.mobile.ui.navigation

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
    navController: NavHostController,
    windowSize: WindowWidthSizeClass
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
                    navController.popBackStack(LandingDestination.route, inclusive = false)
                },
                navigateToHome = {
                    navController.navigate(HomeDestination.route)
                }
            )
        }

        // Pantalla home
        composable(route = HomeDestination.route) {
            HomeScreen(
                navController = navController,
                onViajeClicked = {
                    //TODO
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
            PerfilScreen(navController = navController)
        }

    }

}