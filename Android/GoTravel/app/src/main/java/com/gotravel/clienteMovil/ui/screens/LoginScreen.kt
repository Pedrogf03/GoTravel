package com.gotravel.clienteMovil.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.clienteMovil.R
import com.gotravel.clienteMovil.ui.AppViewModelProvider
import com.gotravel.clienteMovil.ui.GoTravelTopAppBar
import com.gotravel.clienteMovil.ui.navigation.NavigationDestination
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.util.Properties


object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.app_name
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val uiState by viewModel.uiState.collectAsState()

    var email by remember{ mutableStateOf("") }
    var passwd by remember{ mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Scaffold (
        topBar = {
            GoTravelTopAppBar(
                title = stringResource(LoginDestination.titleRes),
                canNavigateBack = false
            )
        }
    ){ innerPading ->

        Column (
            modifier = modifier
                .padding(innerPading)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Text(text = "Email")
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )
            Text(text = "Contraseña")
            TextField(
                value = passwd,
                onValueChange = { passwd = it },
                label = { Text(stringResource(R.string.passwd_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            )
            
            Text(text = mensajeError)

            val context = LocalContext.current
            Button(onClick = {
                if(!(email.isBlank() || email.isEmpty()) && !(passwd.isBlank() || passwd.isEmpty())) {

                    var dirIp = "192.168.1.39"
                    var puerto = 8484

                    val conf = Properties()
                    try {
                        val inputStream = context.assets.open("client.properties")
                        conf.load(inputStream)
                        puerto = Integer.parseInt(conf.getProperty("PUERTO"))
                        dirIp = conf.getProperty("IP")
                        println("$puerto, $dirIp")
                    } catch (e: IOException) {
                        println("No se ha podido leer el archivo de propiedades")
                    }

                    GlobalScope.launch {
                        try {

                            val cliente = Socket(dirIp, puerto)

                            viewModel.updateSocket(cliente)

                            val entrada = DataInputStream(cliente.getInputStream())
                            val salida = DataOutputStream(cliente.getOutputStream())
                            salida.writeUTF("login;$email;$passwd")
                            if (entrada.readBoolean()) {
                                mensajeError = "Sesión iniciada correctamente"
                            } else {
                                mensajeError = "Usuario o contraseña incorrectos"
                            }

                        } catch (e: IOException) {
                            mensajeError = "No se ha podido conectar con el servidor"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                } else {
                    mensajeError = "Por favor, rellena todos los campos"
                }
            }) {
                Text(text = "Iniciar Sesión")
            }

        }

    }

}

