package com.gotravel.mobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Usuario
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodel.CredencialesViewModel
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

@Composable
fun CredencialesScreen(
    modifier: Modifier = Modifier,
    viewModel: CredencialesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateUp: () -> Unit
) {

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (viewModel.opcion == "login") "Iniciar Sesión" else "Registrarse",
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            if(viewModel.opcion == "login") {
                LoginScreen(viewModel)
            } else {
                RegisterScreen(viewModel)
            }

        }

    }

}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LoginScreen(viewModel: CredencialesViewModel) {

    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

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
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = ""
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
    )

    Spacer(modifier = Modifier.padding(8.dp))

    val context = LocalContext.current
    Button(onClick = {
        GlobalScope.launch {
            val usuario: Usuario? = viewModel.iniciarSesion(email, contrasena, context)
            if(usuario != null) {
                withContext(Dispatchers.Main) {
                    //navigateToHome(Usuario)
                    println(usuario.toString())
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
        ){
            Text(text = "Continuar")
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
        }
    }

    Text(
        text = mensajeUi.value,
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
    )

}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun RegisterScreen(viewModel: CredencialesViewModel) {

    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }

    val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

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
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
    )

    Spacer(modifier = Modifier.padding(8.dp))

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
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = ""
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
    )

    Spacer(modifier = Modifier.padding(8.dp))

    val context = LocalContext.current
    Button(onClick = {
        GlobalScope.launch {
            val usuario: Usuario? = viewModel.registrarse(nombre, email, contrasena, context)
            if(usuario != null) {
                withContext(Dispatchers.Main) {
                    //navigateToHome(Usuario)
                    println(usuario.toString())
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
        ){
            Text(text = "Continuar")
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
        }
    }

    Text(
        text = mensajeUi.value,
        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
    )

}