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
import com.google.gson.GsonBuilder
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.App
import com.gotravel.mobile.ui.screen.HomeDestination
import com.gotravel.mobile.ui.screen.SuscripcionDestination
import com.gotravel.mobile.ui.screen.ViajeDestination
import com.gotravel.mobile.ui.theme.GoTravelTheme
import com.gotravel.mobile.ui.utils.Sesion
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
                "subscription_returnurl" -> {

                    // Se obtiene el id de la suscripcion de la url de devolucion de paypal
                    val subscriptionId = uri.getQueryParameter("subscription_id")

                    GlobalScope.launch {

                        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                            withContext(Dispatchers.IO) {
                                val gson = GsonBuilder()
                                    .serializeNulls()
                                    .setLenient()
                                    .create()

                                try {

                                    // Se le envia el id al servidor
                                    Sesion.salida.writeUTF("$subscriptionId")
                                    Sesion.salida.flush()

                                    // El servidor devuelve el usuario actualizado, con el rol y la suscripcion
                                    val jsonFromServer = Sesion.entrada.readUTF()
                                    val usuario : Usuario? = gson.fromJson(jsonFromServer, Usuario::class.java)

                                    if(usuario != null) {
                                        Sesion.usuario = usuario.copy(foto = Sesion.usuario.foto)
                                        withContext(Dispatchers.Main) {
                                            navController.navigate(HomeDestination.route)
                                        }
                                    }

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    Sesion.socket!!.close()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }
                        }

                    }

                }
                "subscription_cancelurl" -> {
                    navController.navigate("${SuscripcionDestination.route}/false")
                }
                "checkout_returnurl" -> {

                    val contratacionId = uri.getQueryParameter("token")

                    println(contratacionId)

                    GlobalScope.launch {

                        if(Sesion.socket != null && !Sesion.socket!!.isClosed) {
                            withContext(Dispatchers.IO) {

                                try {

                                    // Se le envia el id al servidor
                                    Sesion.salida.writeUTF("$contratacionId")
                                    Sesion.salida.flush()

                                    // El servidor devuelve el usuario actualizado, con el rol y la suscripcion
                                    val idViaje = Sesion.entrada.readUTF()

                                    if(idViaje != null) {
                                        withContext(Dispatchers.Main){
                                            navController.navigate("${ViajeDestination.route}/$idViaje")
                                        }
                                    }

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    Sesion.socket!!.close()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }
                        }

                    }

                }
                "checkout_cancelurl" -> {
                    //Ignore
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
            if(Sesion.socket != null && Sesion.socket!!.isConnected) {
                Sesion.salida.writeUTF("cerrarSesion")
                Sesion.salida.flush()

                Sesion.salida.close()
                Sesion.entrada.close()
                Sesion.socket?.close()
            }
        } catch (e: IOException) {
            // IGNORE
        }
    }
}

