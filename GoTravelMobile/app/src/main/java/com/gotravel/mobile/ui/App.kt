package com.gotravel.mobile.ui

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.navigation.AppNavHost


@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    windowSize: WindowWidthSizeClass,
) {

    AppNavHost(
        navController = navController,
        windowSize = windowSize
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar (
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                //style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )

}

sealed class Screen(val route: String, val label: String, val icon: Int, val iconSelected: Int) {
    object Viajes : Screen("viajes", "Viajes",
        R.drawable.outline_location_on_24,
        R.drawable.baseline_location_on_24
    )
    object Servicios : Screen("servicios", "Servicios",
        R.drawable.baseline_add_circle_outline_24,
        R.drawable.baseline_add_circle_24
    )
    object Chats : Screen("chats", "Chats", R.drawable.outline_send_24, R.drawable.baseline_send_24)
    object Perfil : Screen("perfil", "Perfil",
        R.drawable.outline_person_24,
        R.drawable.baseline_person_24
    )
}

val items = listOf(Screen.Viajes, Screen.Servicios, Screen.Chats, Screen.Perfil)

@Composable
fun AppBottomBar(
    currentRoute: String,
    navController: NavHostController,
    usuarioId: Int
) {
    BottomNavigation (
        //backgroundColor = MaterialTheme.colorScheme.primary
    ){

        items.forEach { screen ->
            var selected = false;

            if(currentRoute == screen.route) {
                selected = true
            }
            BottomNavigationItem(
                icon = { Icon(
                    painterResource(id = if(selected) screen.iconSelected else screen.icon),
                    contentDescription = null,
                    //tint = MaterialTheme.colorScheme.onPrimary
                ) },
                label = { Text(
                    text = screen.label,
                    //color = MaterialTheme.colorScheme.onPrimary
                ) },
                selected = selected,
                onClick = {
                    navController.navigate("${screen.route}/${usuarioId}")
                }
            )
        }
    }
}