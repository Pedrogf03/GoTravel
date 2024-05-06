package com.gotravel.mobile.ui.navigation

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
import com.gotravel.mobile.ui.screen.LandingDestination
import com.gotravel.mobile.ui.screen.LandingScreen

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

        // Pantalla para introducir los credenciales de inicio de sesión
        composable(
            route = CredencialesDestination.routeWithArgs,
            arguments = listOf(
                navArgument(CredencialesDestination.opcion) { type = NavType.StringType },
            )) {
            CredencialesScreen(
                navigateUp = {
                    navController.popBackStack(LandingDestination.route, inclusive = false)
                }
            )
        }

    }

}