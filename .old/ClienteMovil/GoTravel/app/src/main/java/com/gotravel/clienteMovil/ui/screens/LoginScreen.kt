package com.gotravel.clienteMovil.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.clienteMovil.R
import com.gotravel.clienteMovil.ui.AppViewModelProvider
import com.gotravel.clienteMovil.ui.GoTravelTopAppBar
import com.gotravel.clienteMovil.ui.navigation.NavigationDestination
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToHome: (Int) -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

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

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                modifier = Modifier
                    .padding(innerPading)
                    .padding(horizontal = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {

                Text(
                    text = if(registro) "Registrarse" else "Iniciar Sesión",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Column(
                    modifier = modifier
                        .wrapContentSize(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if(registro) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            shape = RoundedCornerShape(16.dp),
                            label = { Text(stringResource(R.string.username_label)) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = ""
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        shape = RoundedCornerShape(16.dp),
                        label = { Text(stringResource(R.string.email_label)) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = ""
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        shape = RoundedCornerShape(16.dp),
                        label = { Text(stringResource(R.string.passwd_label)) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = ""
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                Icon(
                                    painter = painterResource(id = if (passwordVisibility) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_24),
                                    contentDescription = "Mostrar/Ocultar contraseña"
                                )
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    )

                    Text(text = mensajeUi.value)

                    val context = LocalContext.current
                    Button(onClick = {
                        GlobalScope.launch {
                            val id: Int? = if (registro) viewModel.registrarse(nombre, email, contrasena, context) else viewModel.iniciarSesion(email, contrasena, context)
                            if(id != null) {
                                withContext(Dispatchers.Main) {
                                    navigateToHome(id)
                                }
                            }
                        }
                    },
                        modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(text = if(registro) "Registrarse" else "Iniciar Sesión")
                    }

                    Text(
                        text = if (registro) "¿Ya estás registrado?" else "¿Aún no estás registrado?",
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { registro = !registro },
                        textDecoration = TextDecoration.Underline
                    )

                }
            }
        }

    }
}

