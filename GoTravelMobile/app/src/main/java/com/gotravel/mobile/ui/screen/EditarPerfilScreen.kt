package com.gotravel.mobile.ui.screen

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Rol
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.PerfilViewModel
import com.gotravel.mobile.ui.utils.AppUiState
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object EditarPerfilDestination : NavDestination {
    override val route = "editarPerfil"
    override val titleRes = R.string.app_name
}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditarPerfilScreen(
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    navigateToCambiarContrasena: () -> Unit
) {

    val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = PerfilDestination.titleRes),
                canNavigateBack = true,
                navigateUp = { navController.navigateUp() }
            )
        },
        modifier = modifier
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            val context = LocalContext.current
            val photoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
            ) { uri: Uri? ->
                GlobalScope.launch {
                    if(viewModel.updateFoto(context, uri)) {
                        withContext(Dispatchers.Main) {
                            navController.navigate(PerfilDestination.route)
                        }
                    }
                }
            }

            Column (modifier = Modifier
                .fillMaxSize()
                .weight(0.25f)
            ){

                Row (
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(80f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){

                    if(AppUiState.usuario.foto != null) {
                        Image(
                            bitmap = AppUiState.usuario.imagen,
                            contentDescription = "",
                            modifier = Modifier
                                .clip(CircleShape)
                                .aspectRatio(1f),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painterResource(id = R.drawable.usernofoto),
                            contentDescription = "",
                            modifier = Modifier
                                .clip(CircleShape)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(20f)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append("Cambiar")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        textAlign = TextAlign.Center
                    )
                }

            }

            Column (modifier = Modifier
                .fillMaxSize()
                .weight(0.75f)
                .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                var nombre by remember { mutableStateOf(AppUiState.usuario.nombre) }
                var apellidos by remember { mutableStateOf(AppUiState.usuario.getApellidos) }
                var email by remember { mutableStateOf(AppUiState.usuario.email) }
                var tfno by remember { mutableStateOf(AppUiState.usuario.getTfno) }

                var campoEditado by remember { mutableStateOf(false) }

                if(AppUiState.usuario.nombre != nombre || AppUiState.usuario.email != email || AppUiState.usuario.getApellidos != apellidos || AppUiState.usuario.getTfno != tfno) {
                    campoEditado = true
                } else {
                    campoEditado = false
                }

                nombre = profileTextField(
                    text = nombre,
                    label = "Nombre",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.padding(8.dp))

                apellidos = profileTextField(
                    text = apellidos,
                    label = "Apellidos",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.padding(8.dp))

                email = profileTextField(
                    text = email,
                    label = "Email",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.padding(8.dp))

                tfno = profileTextField(
                    text = tfno,
                    label = "Telefono",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    onClick = {
                        GlobalScope.launch {
                            if (viewModel.updateUsuario(AppUiState.usuario.copy(nombre = nombre, apellidos = apellidos.ifBlank { null }, email = email, tfno = tfno.ifBlank { null }, foto = null))) {
                                withContext(Dispatchers.Main) {
                                    navController.navigate(PerfilDestination.route)
                                }
                            }
                        }
                    },
                    enabled = campoEditado
                ) {
                    Text(
                        text = "Actualizar datos",
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("Cambiar contraseña")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigateToCambiarContrasena()
                        },
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = mensajeUi.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

            }

        }

    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun profileTextField(
    text: String,
    label: String,
    keyboardType: KeyboardType
) : String {

    var value by remember { mutableStateOf(text) }
    var soloLectura by remember { mutableStateOf(true) }

    TextField(
        value = value,
        onValueChange = { value = it },
        label = { Text(label) },
        singleLine = true,
        readOnly = soloLectura,
        trailingIcon = {
            IconButton(onClick = {
                soloLectura = false
            }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "")
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )

    return value

}