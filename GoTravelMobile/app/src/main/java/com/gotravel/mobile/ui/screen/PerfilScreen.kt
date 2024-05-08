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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.AppUiState
import com.gotravel.mobile.ui.screen.viewmodels.PerfilViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PerfilDestination : NavDestination {
    override val route = "perfil"
    override val titleRes = R.string.app_name
}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
) {

    val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = PerfilDestination.titleRes),
                canNavigateBack = false
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = PerfilDestination.route,
                navController = navController
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
                .weight(0.25f),
                horizontalAlignment = Alignment.CenterHorizontally
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


            Column(modifier = Modifier
                .fillMaxSize()
                .weight(0.75f)
                .padding(top = 32.dp)
            ) {

                var nombre by remember { mutableStateOf(AppUiState.usuario.nombre) }
                var apellidos by remember { mutableStateOf("") }
                if(AppUiState.usuario.apellidos != null) {
                    apellidos = AppUiState.usuario.apellidos!!
                }
                var email by remember { mutableStateOf(AppUiState.usuario.email) }
                var tfno by remember { mutableStateOf("") }
                if(AppUiState.usuario.tfno != null) {
                    tfno = AppUiState.usuario.tfno!!
                }

                nombre = profileTextField(
                    text = nombre,
                    label = "Nombre",
                    updateUsuario = {
                        GlobalScope.launch {
                            viewModel.updateNombre(nombre)
                        }
                    }
                )

                Spacer(modifier = Modifier.padding(8.dp))

                apellidos = profileTextField(
                    text = apellidos,
                    label = "Apellidos",
                    updateUsuario = {
                        GlobalScope.launch {
                            viewModel.updateApellidos(apellidos)
                        }
                    }
                )

                Spacer(modifier = Modifier.padding(8.dp))

                email = profileTextField(
                    text = email,
                    label = "Email",
                    updateUsuario = {
                        GlobalScope.launch {
                            viewModel.updateEmail(email)
                        }
                    }
                )

                Spacer(modifier = Modifier.padding(8.dp))

                tfno = profileTextField(
                    text = tfno,
                    label = "Telefono",
                    updateUsuario = {
                        GlobalScope.launch {
                            viewModel.updateTfno(tfno)
                        }
                    }
                )

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
    updateUsuario: (String) -> Unit
) : String {

    var value by remember { mutableStateOf(text) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = { value = it },
            label = { Text(label) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("Cambiar")
                }
            },
            modifier = Modifier
                .padding(top = 32.dp)
                .clickable { updateUsuario(value) }
        )


    }

    return value

}
