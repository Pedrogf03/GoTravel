package com.gotravel.clienteMovil.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gotravel.clienteMovil.ui.screens.LoginDestination
import com.gotravel.clienteMovil.ui.screens.LoginScreen

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
            LoginScreen()
        }

    }

}