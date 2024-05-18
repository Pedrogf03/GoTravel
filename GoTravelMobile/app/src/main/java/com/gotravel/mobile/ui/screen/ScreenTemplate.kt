package com.gotravel.mobile.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.Screen

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    navigateToStart: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_signal_wifi_connected_no_internet_4_24),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
        )
        Button(onClick = navigateToStart) {
            Text(text = "Reintentar")
        }
    }
}

@Composable
fun LandingLoadingScreen(modifier: Modifier = Modifier) {

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Image(
            modifier = modifier.size(70.dp),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = ""
        )

    }

}

@Composable
fun AppLoadingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    elementosDeNavegacion: List<Screen>
) {

    Scaffold (
        topBar = {
            AppTopBar(
                title = stringResource(id = HomeDestination.titleRes),
                canNavigateBack = false
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = HomeDestination.route,
                navController = navController,
                items = elementosDeNavegacion
            )
        },
        modifier = modifier
    ){
        Column (
            modifier = Modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Image(
                modifier = modifier.size(70.dp),
                painter = painterResource(R.drawable.loading_img),
                contentDescription = ""
            )

        }
    }

}

