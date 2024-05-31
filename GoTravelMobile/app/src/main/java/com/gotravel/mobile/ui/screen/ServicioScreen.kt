package com.gotravel.mobile.ui.screen

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Resena
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.CrearServicioUiState
import com.gotravel.mobile.ui.screen.viewmodels.CrearServicioViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ServicioUiState
import com.gotravel.mobile.ui.screen.viewmodels.ServicioViewModel
import com.gotravel.mobile.ui.utils.Sesion
import com.gotravel.mobile.ui.utils.formatoFinal
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

object ServicioDestination : NavDestination {
    override val route = "servicio"
    override val titleRes = R.string.app_name
    const val idServicio = "idServicio"
    const val idEtapa = "idEtapa"
    val routeWith1Arg = "$route/{$idServicio}"
    val routeWith2Args = "$route/{$idServicio}/{$idEtapa}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServicioScreen(
    viewModel: ServicioViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateUp: () -> Unit,
    actualizarPagina: (Int) -> Unit,
    navigateToStart: () -> Unit,
    navigateToChat: (Int) -> Unit
) {

    when (val uiState = viewModel.uiState) {
        is ServicioUiState.Loading -> {
            LandingLoadingScreen()
        }
        is ServicioUiState.Success -> {

            Scaffold (
                topBar = {
                    AppTopBar(
                        title = stringResource(id = ViajeDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navigateUp() }
                    )
                }
            ){ innerPadding ->

                InformacionServicio(
                    servicio = uiState.servicio,
                    navigateToServicio = {
                        actualizarPagina(it)
                    },
                    viewModel = viewModel,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(innerPadding)
                        .fillMaxSize(),
                    miServicio = uiState.servicio.usuario!!.id == Sesion.usuario.id,
                    navigateToStart = {
                        navigateToStart()
                    },
                    navigateToChat = navigateToChat
                )

            }

        }
        else -> ErrorScreen(navigateToStart = navigateToStart)
    }

}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InformacionServicio(
    modifier: Modifier = Modifier,
    servicio: Servicio,
    viewModel: ServicioViewModel,
    miServicio: Boolean,
    navigateToStart: () -> Unit,
    navigateToServicio: (Int) -> Unit,
    navigateToChat: (Int) -> Unit
) {

    var errorPublicar by remember { mutableStateOf(false) }

    if(errorPublicar) {
        Dialog(onDismissRequest = { errorPublicar = false }) {
            Card{
                Column (
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(imageVector = Icons.Default.Info, contentDescription = "")
                    Text(text = "No puedes publicar un servicio que ya ha finalizado", textAlign = TextAlign.Center)
                }
            }
        }
    }

    var errorFotos by remember { mutableStateOf(false) }

    if(errorFotos) {
        Dialog(onDismissRequest = { errorFotos = false }) {
            Card{
                Column (
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(imageVector = Icons.Default.Info, contentDescription = "")
                    Text(text = "No puedes publicar un servicio sin imagenes", textAlign = TextAlign.Center)
                }
            }
        }
    }

    var editarServicio by remember { mutableStateOf(false) }

    if (editarServicio) {
        Dialog(onDismissRequest = { editarServicio = false }) {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val crearServicioViewModel : CrearServicioViewModel = viewModel(factory = AppViewModelProvider.Factory)

                    when (val uiState = crearServicioViewModel.uiState) {
                        is CrearServicioUiState.Loading -> {
                            LandingLoadingScreen()
                        }
                        is CrearServicioUiState.Success -> {
                            CrearServicioContent(
                                viewModel = crearServicioViewModel,
                                navigateToServicio = navigateToServicio,
                                tiposServicio = uiState.tiposServicio,
                                servicio = servicio,
                                closeEditarServicio = { editarServicio = false },
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        else -> ErrorScreen(
                            navigateToStart = {
                                navigateToStart()
                            }
                        )
                    }
                }
            }

        }
    }

    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        GlobalScope.launch {
            viewModel.subirFoto(context, uri)
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (servicio.imagenes.isNotEmpty()) {
                    var imagen by remember { mutableIntStateOf(0) }

                    Image(
                        bitmap = servicio.imagenes[imagen].foto,
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        contentScale = ContentScale.Crop
                    )

                    Button(
                        onClick = {
                            var index = imagen
                            index--
                            if(index < 0) {
                                imagen = servicio.imagenes.size - 1
                            } else {
                                imagen--
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "",
                            modifier = Modifier
                                .size(12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            var index = imagen
                            index++
                            if(index == servicio.imagenes.size) {
                                imagen = 0
                            } else {
                                imagen++
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "",
                            modifier = Modifier
                                .size(12.dp)
                        )
                    }

                    if(miServicio && servicio.publicado != "1") {
                        Button(
                            onClick = {
                                GlobalScope.launch {
                                    viewModel.deleteFoto(
                                        servicio.imagenes[imagen],
                                        onImageDeleted = {
                                            imagen = 0
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(12.dp)
                            )
                        }
                    }

                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Column (
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text("Selecciona una imagen de tu dispositivo")
                        }
                    }
                }

            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ){

                if(miServicio) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Text(text = "Añadir foto", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        Button(onClick = {
                            editarServicio = true
                        }) {
                            Text(text = "Editar", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        if(servicio.publicado == "0") {
                            Button(onClick = {
                                if(servicio.final.isNotBlank()) {
                                    val final = LocalDate.parse(servicio.final, formatoFinal)
                                    if(final.isBefore(LocalDate.now())) {
                                        errorPublicar = true
                                    } else if (servicio.imagenes.isEmpty()){
                                        errorFotos = true
                                    } else {
                                        GlobalScope.launch {
                                            viewModel.publicarServicio()
                                        }
                                    }
                                } else {
                                    val fecha = LocalDate.parse(servicio.inicio, formatoFinal)
                                    if(fecha.isBefore(LocalDate.now())) {
                                        errorPublicar = true
                                    } else if (servicio.imagenes.isEmpty()){
                                        errorFotos = true
                                    } else {
                                        GlobalScope.launch {
                                            viewModel.publicarServicio()
                                        }
                                    }
                                }
                            }) {
                                Text(text = "Publicar", fontSize = 12.sp)
                            }
                        } else {
                            Button(onClick = {
                                GlobalScope.launch {
                                    viewModel.ocultarServicio()
                                }
                            }) {
                                Text(text = "Ocultar", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(!servicio.contratado){
                            Button(onClick = {
                                GlobalScope.launch {
                                    viewModel.contratarServicio(context)
                                }
                            }) {
                                Text(text = "Contratar")
                            }
                        }
                        Button(onClick = {
                            navigateToChat(servicio.usuario!!.id!!)
                        }) {
                            Text(text = "Chatear")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(servicio.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(text = "" + servicio.precio + "€", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.padding(4.dp))
                servicio.descripcion?.let {
                    Text(it)
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                Text(text = servicio.inicio + " - " + servicio.final.ifBlank { servicio.hora })
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = servicio.tipoServicio.nombre)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Dirección: ", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = servicio.direccion.linea1 + if(servicio.direccion.linea2 != null) ", " + servicio.direccion.linea2 else "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = servicio.direccion.ciudad + ", " + servicio.direccion.estado + ", " + servicio.direccion.pais)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = servicio.direccion.cp)
                Spacer(modifier = Modifier.padding(4.dp))
                if(!miServicio){
                    Text(
                        text = servicio.usuario!!.nombre + " " + servicio.usuario!!.apellidos,
                        modifier = Modifier.clickable {
                            navigateToChat(servicio.usuario!!.id!!)
                        }
                    )
                }

                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {

                    if(!miServicio) {

                        var addResena by remember { mutableStateOf(false) }

                        if(addResena) {
                            Dialog(onDismissRequest = { addResena = false }) {
                                Card (
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                                ){

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = { addResena = false }) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {

                                        val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

                                        Text("Añade una reseña", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

                                        var nota by remember { mutableStateOf("") }
                                        var comentario by remember { mutableStateOf("") }
                                        var seleccionarPuntuacion by remember { mutableStateOf(false) }

                                        TextField(
                                            value = nota,
                                            onValueChange = { nota = it },
                                            label = { Text("Puntuación") },
                                            singleLine = true,
                                            trailingIcon = {
                                                IconButton(onClick = { seleccionarPuntuacion = !seleccionarPuntuacion }) {
                                                    Icon(imageVector = Icons.Default.Star, contentDescription = "")
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
                                            readOnly = true,
                                            modifier = Modifier
                                                .fillMaxWidth()

                                        )

                                        if (seleccionarPuntuacion) {
                                            MyPuntuacionSelector(
                                                onPuntuacionSelected = { nota = it },
                                                onDismiss = { seleccionarPuntuacion = !seleccionarPuntuacion }
                                            )
                                        }

                                        Spacer(modifier = Modifier.padding(8.dp))

                                        TextField(
                                            value = comentario,
                                            onValueChange = { comentario = it },
                                            label = { Text("Comentario*") },
                                            singleLine = false,
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
                                                .height(100.dp)
                                        )

                                        Spacer(modifier = Modifier.padding(4.dp))

                                        Text(
                                            text = mensajeUi.value,
                                            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                                            color = MaterialTheme.colorScheme.error
                                        )

                                        Spacer(modifier = Modifier.padding(4.dp))

                                        Button(
                                            onClick = {
                                                GlobalScope.launch {
                                                    if(viewModel.addResena(nota = nota, comentario = comentario)) {
                                                        addResena = false
                                                    }
                                                }
                                            }
                                        ) {
                                            Text(text = "Publicar")
                                        }

                                    }

                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Button(
                                onClick = {
                                    addResena = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
                            ) {
                                Text(text = "Añadir reseña", color = MaterialTheme.colorScheme.primary)
                            }

                        }
                    }

                    if(servicio.resenas.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = ""
                            )
                            Text(text = "Aún no hay reseñas")
                        }
                    } else {
                        Column {
                            servicio.resenas.forEach {resena ->
                                ResenaCard(
                                    resena = resena
                                )
                            }
                        }
                    }
                }

            }
        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ResenaCard(
    resena: Resena,
    modifier: Modifier = Modifier
) {
    Card (
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier.padding(8.dp)
    ){
        Column (
            modifier = Modifier
                .padding(8.dp)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center
        ){

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row (
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                ){
                    if(resena.usuario!!.foto != null) {
                        Image(
                            bitmap = resena.usuario.imagen!!,
                            contentDescription = "",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .aspectRatio(1f)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painterResource(id = R.drawable.usernofoto),
                            contentDescription = "",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                    
                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(text = resena.usuario.nombre + " " + resena.usuario.getApellidos, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                }

                Row (
                    verticalAlignment = Alignment.Top
                ){
                    for(s in 1 .. resena.puntuacion) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }

            }

            Text(text = resena.contenido, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)

        }
    }
}