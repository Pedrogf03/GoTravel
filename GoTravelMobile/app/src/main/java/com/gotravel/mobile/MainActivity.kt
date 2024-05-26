package com.gotravel.mobile

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gotravel.mobile.ui.App
import com.gotravel.mobile.ui.screen.HomeDestination
import com.gotravel.mobile.ui.theme.GoTravelTheme
import com.gotravel.mobile.ui.utils.AppUiState
import com.gotravel.mobile.ui.utils.PayPalSubscriptions
import com.gotravel.mobile.ui.utils.addRolProfesional
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Iniciar el servicio al iniciar la aplicación
        startService(Intent(this, ClosingService::class.java))
        setContent {
            GoTravelTheme {
                // A surface container using the 'background' color from the theme
                navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(navController)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri = intent.data
        if (uri != null && uri.scheme == "gotravel") {
            when (uri.host) {
                "returnurl" -> {

                    val subscriptionId = uri.getQueryParameter("subscription_id")

                    val context = this;

                    GlobalScope.launch {
                        val suscripcion = PayPalSubscriptions(context = context).getSuscription(subscriptionId!!)
                        addRolProfesional(suscripcion)
                        withContext(Dispatchers.Main) {
                            navController.navigate(HomeDestination.route)
                        }
                    }

                }
                "cancelurl" -> {
                    // Aquí puedes manejar el caso de cancelación del pago
                    println("Pago cancelado")
                }
            }
        }
    }

}

class ClosingService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)

        // Handle application closing
        fireClosingNotification()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Close network resources here
        closeNetworkResources()
    }

    private fun fireClosingNotification() {
        Log.i("ClosingService", "CERRANDO APP")
    }

    private fun closeNetworkResources() {
        try{
            if(AppUiState.socket != null) {
                AppUiState.salida.writeUTF("cerrarSesion")
                AppUiState.salida.flush()

                AppUiState.salida.close()
                AppUiState.entrada.close()
                AppUiState.socket?.close()
            }
        } catch (e: IOException) {
            // IGNORE
        }
    }
}

