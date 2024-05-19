package com.gotravel.mobile.ui.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gotravel.mobile.data.model.Rol
import com.gotravel.mobile.ui.Screen
import com.gotravel.mobile.ui.screen.CambiarContrasenaDestination
import com.gotravel.mobile.ui.screen.CambiarContrasenaScreen
import com.gotravel.mobile.ui.screen.CrearViajeDestination
import com.gotravel.mobile.ui.screen.CrearViajeScreen
import com.gotravel.mobile.ui.screen.CredencialesDestination
import com.gotravel.mobile.ui.screen.CredencialesScreen
import com.gotravel.mobile.ui.screen.EditarPerfilDestination
import com.gotravel.mobile.ui.screen.EditarPerfilScreen
import com.gotravel.mobile.ui.screen.HomeDestination
import com.gotravel.mobile.ui.screen.HomeScreen
import com.gotravel.mobile.ui.screen.LandingDestination
import com.gotravel.mobile.ui.screen.LandingScreen
import com.gotravel.mobile.ui.screen.PerfilDestination
import com.gotravel.mobile.ui.screen.PerfilScreen
import com.gotravel.mobile.ui.screen.SuscripcionDestination
import com.gotravel.mobile.ui.screen.SuscripcionScreen
import com.gotravel.mobile.ui.screen.ViajeDestination
import com.gotravel.mobile.ui.screen.ViajeScreen
import com.gotravel.mobile.ui.screen.ViajesDestination
import com.gotravel.mobile.ui.screen.ViajesScreen
import com.gotravel.mobile.ui.utils.AppUiState

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

        composable(route = LandingDestination.route) {

            val context = LocalContext.current
            val sharedPref = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
            val email = sharedPref.getString("email", "")
            val contrasena = sharedPref.getString("contraseña", "")

            if (!email.isNullOrBlank() && !contrasena.isNullOrBlank()) {
                navController.navigate("${CredencialesDestination.route}/login")
            } else {
                LandingScreen(
                    navigateToCredenciales = {
                        navController.navigate("${CredencialesDestination.route}/${it}")
                    }
                )
            }
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
            val items = listOfNotNull(Screen.Inicio, Screen.Viajes, if(AppUiState.usuario.roles.contains(Rol("Profesional"))) Screen.Servicios else null, Screen.Chats, Screen.Perfil)
            HomeScreen(
                navController = navController,
                onViajeClicked = {
                    navController.navigate("${ViajeDestination.route}/${it}")
                },
                navigateToCrearViaje = {
                    navController.navigate(CrearViajeDestination.route)
                },
                elementosDeNavegacion = items,
                navigateToStart = {
                    navController.navigate(LandingDestination.route)
                }
            )
        }

        // Pantalla viajes sin busqueda
        composable(route = ViajesDestination.route) {
            val items = listOfNotNull(Screen.Inicio, Screen.Viajes, if(AppUiState.usuario.roles.contains(Rol("Profesional"))) Screen.Servicios else null, Screen.Chats, Screen.Perfil)
            ViajesScreen(
                navController = navController,
                buscarViaje = { busqueda ->
                    navController.navigate(ViajesDestination.route + if(busqueda.isNotBlank()) "/${busqueda}" else "")
                },
                onViajeClicked = {
                    navController.navigate("${ViajeDestination.route}/${it}")
                },
                elementosDeNavegacion = items,
                navigateToStart = {
                    navController.navigate(LandingDestination.route)
                }
            )
        }

        // Pantalla viajes con busqueda
        composable(
            route = ViajesDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ViajesDestination.busqueda) { type = NavType.StringType },
            )) {
            val items = listOfNotNull(Screen.Inicio, Screen.Viajes, if(AppUiState.usuario.roles.contains(Rol("Profesional"))) Screen.Servicios else null, Screen.Chats, Screen.Perfil)
            ViajesScreen(
                navController = navController,
                buscarViaje = { busqueda ->
                    navController.navigate(ViajesDestination.route + if(busqueda.isNotBlank()) "/${busqueda}" else "")
                },
                onViajeClicked = {
                    navController.navigate("${ViajeDestination.route}/${it}")
                },
                elementosDeNavegacion = items,
                navigateToStart = {
                    navController.navigate(LandingDestination.route)
                }
            )
        }

        // Pantalla de un viaje
        composable(
            route = ViajeDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ViajeDestination.idViaje) { type = NavType.IntType },
            )) {
            ViajeScreen(
                navigateUp = { navController.popBackStack(ViajesDestination.route, inclusive = false) },
                actualizarPagina = {
                    navController.navigate("${ViajeDestination.route}/${it}")
                },
                navigateToStart = {
                    navController.navigate(LandingDestination.route)
                }
            )
        }

        // Pantalla perfil
        composable(route = PerfilDestination.route) {
            val items = listOfNotNull(Screen.Inicio, Screen.Viajes, if(AppUiState.usuario.roles.contains(Rol("Profesional"))) Screen.Servicios else null, Screen.Chats, Screen.Perfil)
            PerfilScreen(
                navController = navController,
                navigateToEditarPerfil = {
                    navController.navigate(EditarPerfilDestination.route)
                },
                navigateToContrataciones = {
                    //TODO
                },
                navigateToMetodosPago = {
                    //TODO
                },
                navigateToPagos = {
                    //TODO
                },
                navigateToSuscripcion = {
                    navController.navigate("${SuscripcionDestination.route}/${it}")
                },
                navigateToStart = {
                    navController.navigate(LandingDestination.route)
                },
                elementosDeNavegacion = items
            )
        }

        // Pantalla para editar perfil
        composable(route = EditarPerfilDestination.route) {
            EditarPerfilScreen(
                navigateToCambiarContrasena = {
                    navController.navigate(CambiarContrasenaDestination.route)
                },
                navController = navController
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
                navigateToViaje = {
                                  navController.navigate(("${ViajeDestination.route}/${it}"))
                },
                navController = navController
            )
        }

        // Pantalla para ver informacion de la suscripcion
        composable(
            route = SuscripcionDestination.routeWithArgs,
            arguments = listOf(
                navArgument(SuscripcionDestination.esProfesional) { type = NavType.BoolType },
            )) {
            SuscripcionScreen(
                navController = navController
            )
        }

    }

}