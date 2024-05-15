package com.gotravel.mobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.navigation.AppNavHost
import com.gotravel.mobile.ui.screen.HomeDestination
import com.gotravel.mobile.ui.screen.ViajesDestination


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    navController: NavHostController = rememberNavController()
) {

    AppNavHost(
        navController = navController
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
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
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
    object Inicio : Screen(HomeDestination.route, "Inicio",
        R.drawable.outline_home_24,
        R.drawable.baseline_home_24
    )
    object Viajes : Screen(ViajesDestination.route, "Viajes",
        R.drawable.outline_location_on_24,
        R.drawable.baseline_location_on_24
    )
    object Servicios : Screen("servicios", "Servicios",
        R.drawable.outline_door_back_24,
        R.drawable.baseline_door_back_24
    )
    object Chats : Screen("chats", "Chats", R.drawable.outline_chat_24, R.drawable.baseline_chat_24)
    object Perfil : Screen("perfil", "Perfil",
        R.drawable.outline_person_24,
        R.drawable.baseline_person_24
    )
}

val items = listOf(Screen.Inicio, Screen.Viajes, Screen.Servicios, Screen.Chats, Screen.Perfil)

@Composable
fun AppBottomBar(
    currentRoute: String,
    navController: NavHostController
) {
    BottomNavigation (
        backgroundColor = MaterialTheme.colorScheme.primary
    ){

        items.forEach { screen ->
            var selected = false

            if(currentRoute == screen.route) {
                selected = true
            }
            BottomNavigationItem(
                icon = { Icon(
                    painterResource(id = if(selected) screen.iconSelected else screen.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) },
                label = { Text(
                    text = screen.label,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall
                ) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route)
                }
            )
        }
    }
}