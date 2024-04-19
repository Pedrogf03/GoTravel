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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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


object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.app_name
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var email by remember { mutableStateOf("") }
    var passwd by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }

    var registro by remember { mutableStateOf(false) }

    val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

    Scaffold(
        topBar = {
            GoTravelTopAppBar(
                title = stringResource(LoginDestination.titleRes),
                canNavigateBack = false
            )
        }
    ) { innerPading ->
        Column(
            modifier = modifier
                .padding(innerPading)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if(registro) {

                Text(text = "Usuario")
                TextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text(stringResource(R.string.username_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
            }

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

            Text(text = mensajeUi.value)

            val context = LocalContext.current
            Button(onClick = {
                if(registro) viewModel.registrarse(usuario, email, passwd, context) else viewModel.iniciarSesion(email, passwd, context)
            }) {
                Text(text = if (registro) "Registrarse" else "Iniciar Sesión")
            }

            Button(onClick = {
                registro = !registro
            }) {
                Text(text = if (registro) "Iniciar Sesión" else "Registrarse")
            }

        }
    }
}


