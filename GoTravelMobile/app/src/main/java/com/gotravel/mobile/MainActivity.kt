package com.gotravel.mobile

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gotravel.mobile.ui.App
import com.gotravel.mobile.ui.screen.LandingDestination
import com.gotravel.mobile.ui.theme.GoTravelTheme
import com.gotravel.mobile.ui.utils.AppUiState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavController
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            GoTravelTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navHostController = rememberNavController()
                    navController = navHostController
                    App(navHostController)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStop() {
        super.onStop()
        navController.navigate(LandingDestination.route)

        if(AppUiState.socket != null) {

            GlobalScope.launch {
                AppUiState.salida.writeUTF("fin")
                AppUiState.salida.flush()


                AppUiState.salida.close()
                AppUiState.entrada.close()
                AppUiState.socket!!.close()
            }

        }

    }

}
