package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Etapa
import com.gotravel.mobile.data.model.Viaje
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.ViajeUiState
import com.gotravel.mobile.ui.screen.viewmodels.ViajeViewModel
import com.gotravel.mobile.ui.utils.formatoFinal
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

object ViajeDestination : NavDestination {
    override val route = "viaje"
    override val titleRes = R.string.app_name
    const val idViaje = "idViaje"
    val routeWithArgs = "$route/{$idViaje}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViajeScreen(
    viewModel: ViajeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateUp: () -> Unit,
    actualizarPagina: (Int) -> Unit,
    navigateToStart: () -> Unit
) {

    when (val uiState = viewModel.uiState) {
        is ViajeUiState.Loading -> {
            LandingLoadingScreen()
        }
        is ViajeUiState.Success -> {

            var finalizado = false

            if(LocalDate.parse(uiState.viaje.final, formatoFinal).isBefore(LocalDate.now())) finalizado = true

            Scaffold (
                topBar = {
                    AppTopBar(
                        title = stringResource(id = ViajeDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navigateUp() }
                    )
                }
            ){

                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    InformacionViaje(
                        viaje = uiState.viaje,
                        actualizarPagina = {
                            actualizarPagina(uiState.viaje.id!!)
                        },
                        finalizado = finalizado,
                        viewModel = viewModel,
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(8.dp)
                    )
                    MostrarEtapas(
                        etapas = uiState.viaje.etapas,
                        actualizarPagina = {
                            actualizarPagina(uiState.viaje.id!!)
                        },
                        viewModel = viewModel,
                        finalizado = finalizado
                    )
                }

            }

        }
        else -> ErrorScreen(navigateToStart = navigateToStart)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InformacionViaje(
    modifier: Modifier = Modifier,
    viaje: Viaje,
    finalizado: Boolean,
    viewModel: ViajeViewModel,
    actualizarPagina: () -> Unit
) {

    var editarViaje by remember { mutableStateOf(false) }

    if (editarViaje) {
        Dialog(onDismissRequest = { editarViaje = false }) {
            EditarViaje(
                viewModel = viewModel,
                cerrarDialogo = { editarViaje = !editarViaje },
                actualizarPagina = actualizarPagina,
                viaje = viaje
            )
        }
    }

    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        Column (
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ){

            if(!finalizado) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(onClick = { editarViaje = !editarViaje }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = ""
                        )
                    }
                }
            }

            Text(text = viaje.nombre, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.padding(8.dp))

            if(viaje.descripcion != null) {
                Text(text = viaje.descripcion, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Justify)
                Spacer(modifier = Modifier.padding(8.dp))
            }

            Text(text = "Desde el ${viaje.inicio} hasta el ${viaje.final}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Justify)

            Spacer(modifier = Modifier.padding(8.dp))

            Text(text = "Precio total: ${viaje.costeTotal}€")

        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MostrarEtapas(
    modifier: Modifier = Modifier,
    etapas: List<Etapa>,
    viewModel: ViajeViewModel,
    actualizarPagina: () -> Unit,
    finalizado: Boolean
) {

    var nuevaEtapa by remember { mutableStateOf(false) }

    if (nuevaEtapa) {
        Dialog(onDismissRequest = { nuevaEtapa = false }) {
            CrearEtapa(
                viewModel = viewModel,
                cerrarDialogo = { nuevaEtapa = !nuevaEtapa },
                actualizarPagina = actualizarPagina
            )
        }
    }

    Card (
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if(!finalizado) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        onClick = { nuevaEtapa = !nuevaEtapa },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "NUEVA ETAPA", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            if(etapas.isEmpty()) {
                Column (
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = "Este viaje aún no tiene etapas", color = MaterialTheme.colorScheme.onSurface)
                }
            } else {

                val fechaActual = LocalDate.now()
                var indiceEtapaActual = etapas.indexOfFirst { etapa ->
                    val inicio = LocalDate.parse(etapa.inicio, formatoFinal)
                    val final = LocalDate.parse(etapa.final, formatoFinal)
                    !fechaActual.isBefore(inicio) && !fechaActual.isAfter(final)
                }

                // Si no se encuentra una etapa en curso, busca la próxima etapa
                if (indiceEtapaActual == -1) {
                    indiceEtapaActual = etapas.indexOfFirst { etapa ->
                        val inicio = LocalDate.parse(etapa.inicio, formatoFinal)
                        fechaActual.isBefore(inicio)
                    }
                }

                val state = rememberLazyListState()

                LazyColumn(state = state) {
                    itemsIndexed(items = etapas) { index, etapa ->
                        EtapaCard(
                            etapa = etapa,
                            color = if(index == indiceEtapaActual) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                LaunchedEffect(key1 = true) {
                    state.animateScrollToItem(index = indiceEtapaActual)
                }
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EtapaCard(
    etapa: Etapa,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card (
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
    ){
        Row (
            modifier = Modifier
                .padding(8.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ){

            var textColor = MaterialTheme.colorScheme.onPrimary

            if(color == MaterialTheme.colorScheme.onPrimary) {
                textColor = MaterialTheme.colorScheme.primary
            }

            Column {
                Text(text = etapa.nombre, color = textColor)
                Text(text = "Tipo " + etapa.tipo, color = textColor, fontSize = 12.sp)
                Text(text = etapa.inicio + " - " + etapa.final, fontSize = 8.sp, color = textColor)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "", tint = textColor)

        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditarViaje(
    viewModel: ViajeViewModel,
    cerrarDialogo: () -> Unit,
    actualizarPagina: () -> Unit,
    viaje: Viaje
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        var nombre by remember { mutableStateOf(viaje.nombre) }
        var descripcion by remember { mutableStateOf(viaje.descripcion ?: "") }
        var fechaInicio by remember { mutableStateOf(viaje.inicio) }
        var fechaFinal by remember { mutableStateOf(viaje.final) }
        var seleccionarFechaInicio by remember { mutableStateOf(false) }
        var seleccionarFechaFinal by remember { mutableStateOf(false) }

        val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ){
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(onClick = { cerrarDialogo() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "")
                    }
                }

                Text(
                    text = "Editar viaje",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del viaje*") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del viaje") },
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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha de inicio*") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { seleccionarFechaInicio = !seleccionarFechaInicio }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                        }
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
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                if(seleccionarFechaInicio) {
                    MyDatePickerDialog(
                        onDateSelected = { fechaInicio = it },
                        onDismiss = { seleccionarFechaInicio = !seleccionarFechaInicio }
                    )
                }

                TextField(
                    value = fechaFinal,
                    onValueChange = { fechaFinal = it },
                    label = { Text("Fecha de final*") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { seleccionarFechaFinal = !seleccionarFechaFinal }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                        }
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
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()

                )

                Spacer(modifier = Modifier.padding(8.dp))

                if(seleccionarFechaFinal) {
                    MyDatePickerDialog(
                        onDateSelected = { fechaFinal = it },
                        onDismiss = { seleccionarFechaFinal = !seleccionarFechaFinal }
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = mensajeUi.value,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )

                Button(
                    onClick = {
                        GlobalScope.launch {
                            if(viewModel.actualizarViaje(nombre = nombre, descripcion = descripcion, fechaInicio = fechaInicio, fechaFin = fechaFinal, viaje = viaje)) {
                                withContext(Dispatchers.Main) {
                                    actualizarPagina()
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
                        Text(text = "Actualizar")
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                    }
                }

            }

        }


    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun CrearEtapa(
    etapa: Etapa? = null,
    cerrarDialogo: () -> Unit,
    viewModel: ViajeViewModel,
    actualizarPagina: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        var nombre by remember { mutableStateOf(etapa?.nombre ?: "") }
        var tipo by remember { mutableStateOf(etapa?.tipo ?: "") }
        var fechaInicio by remember { mutableStateOf(etapa?.inicio ?: "") }
        var fechaFinal by remember { mutableStateOf(etapa?.final ?: "") }
        var seleccionarFechaInicio by remember { mutableStateOf(false) }
        var seleccionarFechaFinal by remember { mutableStateOf(false) }

        val options = listOf("Transporte", "Estancia")
        var expanded by remember { mutableStateOf(false) }

        val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ){
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(onClick = { cerrarDialogo() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "")
                    }
                }

                Text(
                    text = if (etapa != null) "Actualizar etapa" else "Nueva etapa",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la etapa*") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = fechaInicio,
                    onValueChange = { fechaInicio = it },
                    label = { Text("Fecha de inicio*") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { seleccionarFechaInicio = !seleccionarFechaInicio }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                        }
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
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                if(seleccionarFechaInicio) {
                    MyDatePickerDialog(
                        onDateSelected = { fechaInicio = it },
                        onDismiss = { seleccionarFechaInicio = !seleccionarFechaInicio }
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = fechaFinal,
                    onValueChange = { fechaFinal = it },
                    label = { Text("Fecha de final*") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { seleccionarFechaFinal = !seleccionarFechaFinal }) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                        }
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
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                if(seleccionarFechaFinal) {
                    MyDatePickerDialog(
                        onDateSelected = { fechaFinal = it },
                        onDismiss = { seleccionarFechaFinal = !seleccionarFechaFinal }
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(tipo.ifEmpty { "Tipo de etapa*" })
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    tipo = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = mensajeUi.value,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {

                        if(etapa != null) {
                            /*
                            GlobalScope.launch {
                                if(viewModel.actualizarEtapa(etapa.copy(nombre = nombre, fechaInicio = inicio.format(formatoFromDb), fechaFinal = fin.format(formatoFromDb), tipo = tipo))) {
                                    actualizarPagina()
                                }
                            }
                             */
                        } else {
                            GlobalScope.launch {
                                if(viewModel.guardarEtapa(nombre = nombre, fechaInicio = fechaInicio, fechaFinal = fechaFinal, tipo = tipo)) {
                                    withContext(Dispatchers.Main) {
                                        actualizarPagina()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if(etapa != null) "Actualizar" else "Crear")
                }

            }

        }

    }
}

