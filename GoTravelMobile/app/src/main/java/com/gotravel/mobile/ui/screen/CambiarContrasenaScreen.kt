package com.gotravel.mobile.ui.screen

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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.PerfilViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CambiarContrasenaDestination : NavDestination {
    override val route = "cambiarContrasena"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun CambiarContrasenaScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    Scaffold (
        topBar = {
            AppTopBar(
                title = stringResource(id = CredencialesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier
    ){ innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 32.dp, horizontal = 16.dp)
        ) {

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.60f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                var contrasenaActual by remember { mutableStateOf("") }
                var contrasenaNueva by remember { mutableStateOf("") }
                var confirmarContrasena by remember { mutableStateOf("") }

                var verContrasenaActual by remember { mutableStateOf(false) }
                var verContrasenaNueva by remember { mutableStateOf(false) }
                var verConfirmar by remember { mutableStateOf(false) }

                val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

                TextField(
                    value = contrasenaActual,
                    onValueChange = { contrasenaActual = it },
                    label = { Text("Contraseña actual") },
                    singleLine = true,
                    visualTransformation = if (verContrasenaActual) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = ""
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { verContrasenaActual = !verContrasenaActual }) {
                            Icon(
                                painter = painterResource(id = if (verContrasenaActual) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                                contentDescription = "Mostrar/Ocultar contraseña"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction =ImeAction.Next
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = contrasenaNueva,
                    onValueChange = { contrasenaNueva = it },
                    label = { Text("Contraseña nueva") },
                    singleLine = true,
                    visualTransformation = if (verContrasenaNueva) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = ""
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { verContrasenaNueva = !verContrasenaNueva }) {
                            Icon(
                                painter = painterResource(id = if (verContrasenaNueva) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                                contentDescription = "Mostrar/Ocultar contraseña"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction =ImeAction.Next
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

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
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    onClick = {
                        GlobalScope.launch {
                            if(viewModel.updateContrasena(contrasenaActual, contrasenaNueva, confirmarContrasena)) {
                                withContext(Dispatchers.Main) {
                                    navigateUp()
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
                        Text(text = "Cambiar contraseña")
                    }
                }

                Text(
                    text = mensajeUi.value,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )

            }

            Card (
                modifier = Modifier
                    .wrapContentSize()
                    .weight(0.40f),
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