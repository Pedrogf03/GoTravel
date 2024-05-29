package com.gotravel.mobile.ui.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.CredencialesViewModel
import com.gotravel.mobile.ui.utils.Sesion
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CredencialesDestination : NavDestination {
    override val route = "landing"
    override val titleRes = R.string.app_name
    const val opcion = "opcion"
    val routeWithArgs = "$route/{$opcion}"
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun CredencialesScreen(
    modifier: Modifier = Modifier,
    viewModel: CredencialesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateUp: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToCredenciales: (String) -> Unit,
    borrarNavegacion: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
    val email = sharedPref.getString("email", "")
    val contrasena = sharedPref.getString("contraseña", "")

    val sesionIniciada = remember { mutableStateOf(true) }

    if (!Sesion.segundoPlano && sesionIniciada.value && viewModel.opcion == "login") {
        if (!email.isNullOrBlank() && !contrasena.isNullOrBlank()) {
            LaunchedEffect(Unit) {
                GlobalScope.launch {
                    val sesion = viewModel.conectarConServidor(email, contrasena, context = context)
                    if (sesion) {
                        withContext(Dispatchers.Main) {
                            borrarNavegacion()
                            navigateToHome()
                        }
                    } else {
                        with (sharedPref.edit()) {
                            putBoolean("sesionIniciada", false)
                            apply()
                        }
                        sesionIniciada.value = false
                    }
                }
            }
        } else {
            sesionIniciada.value = false
        }
    }

    if (!sesionIniciada.value || viewModel.opcion == "registro") {
        Pantalla(
            navigateUp,
            modifier,
            viewModel,
            navigateToHome,
            navigateToCredenciales,
            borrarNavegacion
        )
    }
}


@Composable
private fun Pantalla(
    navigateUp: () -> Unit,
    modifier: Modifier,
    viewModel: CredencialesViewModel,
    navigateToHome: () -> Unit,
    navigateToCredenciales: (String) -> Unit,
    borrarNavegacion: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = CredencialesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier
    ) {

        if (viewModel.opcion == "login") {
            LoginScreen(
                viewModel = viewModel,
                registro = false,
                navigateToHome = navigateToHome,
                navigateToCredenciales = navigateToCredenciales,
                modifier = Modifier.padding(it),
                borrarNavegacion = borrarNavegacion
            )
        } else {
            LoginScreen(
                viewModel = viewModel,
                registro = true,
                navigateToHome = navigateToHome,
                navigateToCredenciales = navigateToCredenciales,
                modifier = Modifier.padding(it),
                borrarNavegacion = borrarNavegacion
            )
        }

    }
}

@OptIn(DelicateCoroutinesApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: CredencialesViewModel,
    registro: Boolean,
    navigateToHome: () -> Unit,
    navigateToCredenciales: (String) -> Unit,
    borrarNavegacion: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 32.dp, horizontal = 16.dp)
    ) {

        Column (
            modifier = Modifier
                .fillMaxSize()
                .weight(0.75f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            var email by remember { mutableStateOf("") }
            var contrasena by remember { mutableStateOf("") }
            var confirmarContrasena by remember { mutableStateOf("") }
            var nombre by remember { mutableStateOf("") }

            var verContrasena by remember { mutableStateOf(false) }
            var verConfirmar by remember { mutableStateOf(false) }

            val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

            if (registro) {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
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
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))
            }

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
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
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = ""
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { verContrasena = !verContrasena }) {
                        Icon(
                            painter = painterResource(id = if (verContrasena) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = if(registro) ImeAction.Next else ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            if (registro) {
                TextField(
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    visualTransformation = if (verConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = ""
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { verConfirmar = !verConfirmar }) {
                            Icon(
                                painter = painterResource(id = if (verConfirmar) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                                contentDescription = "Mostrar/Ocultar contraseña"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))
            }

            val context = LocalContext.current
            Button(
                onClick = {
                    GlobalScope.launch {
                        val sesionIniciada = if (registro) viewModel.conectarConServidor(email, contrasena, nombre, confirmarContrasena, context = context) else viewModel.conectarConServidor(email, contrasena, context = context)
                        if (sesionIniciada) {
                            withContext(Dispatchers.Main) {
                                val sharedPref = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
                                with (sharedPref.edit()) {
                                    putString("email", email)
                                    putString("contraseña", contrasena)
                                    putBoolean("sesionIniciada", true)
                                    apply()
                                }
                                borrarNavegacion()
                                navigateToHome()
                            }
                        } else {
                            val sharedPref = context.getSharedPreferences("credenciales", Context.MODE_PRIVATE)
                            with (sharedPref.edit()) {
                                putBoolean("sesionIniciada", false)
                                apply()
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Continuar")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append(if(registro) "¿Ya tienes una cuenta?" else "¿Aún no estás registrado?")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navigateToCredenciales(if (registro) "login" else "registro")
                    },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(4.dp))

            if(!registro) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("¿Has olvidado tu contraseña?")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // TODO
                        },
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.padding(8.dp))
            }

            Text(
                text = mensajeUi.value,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
            
        }
        
        if(registro) {
            Card (
                modifier = Modifier
                    .wrapContentSize()
                    .weight(0.25f),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ){
                Column(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "La contraseña debe tener al menos 8 caracteres, incluyendo al menos una letra minúscula, una letra mayúscula, un número y un carácter no alfanumérico (¡!¿?_.).",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.padding(8.dp))
                    
                    Text(
                        text = "No puede contener espacios en blanco.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

    }

}