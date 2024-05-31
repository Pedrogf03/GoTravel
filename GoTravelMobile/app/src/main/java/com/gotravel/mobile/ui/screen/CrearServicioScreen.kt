package com.gotravel.mobile.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Servicio
import com.gotravel.mobile.data.model.Tiposervicio
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.CrearServicioUiState
import com.gotravel.mobile.ui.screen.viewmodels.CrearServicioViewModel
import com.gotravel.mobile.ui.utils.formatoFinal
import com.gotravel.mobile.ui.utils.formatoFromDb
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate


object CrearServicioDestination : NavDestination {
    override val route = "crearServicio"
    override val titleRes = R.string.app_name
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CrearServicioScreen(
    modifier: Modifier = Modifier,
    viewModel: CrearServicioViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    navigateToServicio: (Int) -> Unit
) {

    when (val uiState = viewModel.uiState) {
        is CrearServicioUiState.Loading -> {
            LandingLoadingScreen()
        }
        is CrearServicioUiState.Success -> {
            Scaffold (
                topBar = {
                    AppTopBar(
                        title = stringResource(id = CrearViajeDestination.titleRes),
                        canNavigateBack = true,
                        navigateUp = { navController.popBackStack() }
                    )
                },
                modifier = modifier
            ){innerPadding ->

                CrearServicioContent(
                    viewModel = viewModel,
                    modifier = modifier.padding(innerPadding).padding(32.dp),
                    navigateToServicio = navigateToServicio,
                    tiposServicio = uiState.tiposServicio
                )

            }
        }
        is CrearServicioUiState.Error -> {
            ErrorScreen(
                navigateToStart = {
                    navController.navigate(LandingDestination.route)
                }
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@OptIn(DelicateCoroutinesApi::class)
fun CrearServicioContent(
    viewModel: CrearServicioViewModel,
    modifier: Modifier = Modifier,
    navigateToServicio: (Int) -> Unit,
    tiposServicio: List<Tiposervicio>,
    closeEditarServicio: () -> Unit = {},
    servicio: Servicio? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement
    ) {

        var seleccionarInfoBasica by remember { mutableStateOf(true) }
        var nombre by remember { mutableStateOf(servicio?.nombre ?: "") }
        var descripcion by remember { mutableStateOf(servicio?.descripcion ?: "") }
        var precio by remember { mutableStateOf(servicio?.precio.toString()) }
        var tipoServicio by remember { mutableStateOf(servicio?.tipoServicio) }
        var seleccionarTiposervicio by remember { mutableStateOf(false) }

        var seleccionarFechasYHora by remember { mutableStateOf(false) }
        var fechaInicio by remember { mutableStateOf(servicio?.inicio ?: "") }
        var fechaFinal by remember { mutableStateOf(servicio?.final ?: "") }
        var seleccionarFechaInicio by remember { mutableStateOf(false) }
        var seleccionarFechaFinal by remember { mutableStateOf(false) }
        var hora by remember { mutableStateOf(servicio?.hora ?: "") }
        var seleccionarHora by remember { mutableStateOf(false) }

        var seleccionarDireccion by remember { mutableStateOf(false) }

        val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

        if(servicio == null) {
            Text(
                text = "Nuevo servicio",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        ) {

            Column (
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                if(servicio != null) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ){
                        IconButton(onClick = { closeEditarServicio() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }
                }

                if(seleccionarInfoBasica) {

                    Text(
                        text = "Información básica",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre*") },
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
                        label = { Text("Descripción (opcional)") },
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

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = tipoServicio?.nombre ?: "Selecciona un tipo de servicio",
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable { seleccionarTiposervicio = true })
                        DropdownMenu(
                            expanded = seleccionarTiposervicio,
                            onDismissRequest = { seleccionarTiposervicio = false }) {
                            tiposServicio.forEach { tipo ->
                                DropdownMenuItem(
                                    onClick = { tipoServicio = tipo; seleccionarTiposervicio = false },
                                    text = { Text(text = tipo.nombre) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = mensajeUi.value,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Button(
                        onClick = {
                            if(viewModel.validarInfoBasica(nombre, descripcion.ifBlank { null }, precio, tipoServicio)){
                                seleccionarInfoBasica = false
                                seleccionarFechasYHora = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Siguiente", color = MaterialTheme.colorScheme.onPrimary)
                    }

                } else if (seleccionarFechasYHora) {

                    Text(
                        text = "Fechas y Hora",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
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

                    if (seleccionarFechaInicio) {
                        MyDatePickerDialog(
                            onDateSelected = { fechaInicio = it },
                            onDismiss = { seleccionarFechaInicio = !seleccionarFechaInicio }
                        )
                    }

                    TextField(
                        value = fechaFinal,
                        onValueChange = { fechaFinal = it },
                        label = { Text("Fecha de final (Opcional)") },
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

                    if (seleccionarFechaFinal) {
                        MyDatePickerDialog(
                            onDateSelected = { fechaFinal = it },
                            onDismiss = { seleccionarFechaFinal = !seleccionarFechaFinal }
                        )
                    }

                    TextField(
                        value = hora,
                        onValueChange = { hora = it },
                        label = { Text("Hora (Opcional)") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { seleccionarHora = !seleccionarHora }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_access_time_24),
                                    contentDescription = ""
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
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()

                    )

                    if (seleccionarHora) {
                        MyTimePickerDialog(
                            onTimeSelected = { hora = it },
                            onDismiss = { seleccionarHora = !seleccionarHora }
                        )
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = mensajeUi.value,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                seleccionarFechasYHora = false
                                seleccionarInfoBasica = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f),
                        ) {
                            Text(text = "Volver", color = MaterialTheme.colorScheme.onPrimary)
                        }
                        
                        Spacer(modifier = Modifier.padding(8.dp))

                        Button(
                            onClick = {
                                if(viewModel.validarFechasYHora(fechaInicio, fechaFinal.ifBlank { null }, hora.ifBlank { null })){
                                    seleccionarFechasYHora = false
                                    seleccionarDireccion = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f),
                        ) {
                            Text(text = "Siguiente", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                } else if (seleccionarDireccion) {

                    Text(
                        text = "Dirección",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    var linea1 by remember { mutableStateOf(servicio?.direccion?.linea1 ?: "") }
                    var linea2 by remember { mutableStateOf(servicio?.direccion?.linea2 ?: "") }
                    var ciudad by remember { mutableStateOf(servicio?.direccion?.ciudad ?: "") }
                    var estado by remember { mutableStateOf(servicio?.direccion?.estado ?: "") }
                    var pais by remember { mutableStateOf(servicio?.direccion?.pais ?: "") }
                    var cp by remember { mutableStateOf(servicio?.direccion?.cp ?: "") }

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = linea1,
                        onValueChange = { linea1 = it },
                        label = { Text("Linea 1 de dirección*") },
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
                        value = linea2,
                        onValueChange = { linea2 = it },
                        label = { Text("Linea 2 de dirección (opcional)") },
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
                        value = ciudad,
                        onValueChange = { ciudad = it },
                        label = { Text("Ciudad*") },
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
                        value = estado,
                        onValueChange = { estado = it },
                        label = { Text("Estado*") },
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
                        value = pais,
                        onValueChange = { pais = it },
                        label = { Text("País*") },
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
                        value = cp,
                        onValueChange = { cp = it },
                        label = { Text("CP*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = mensajeUi.value,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                seleccionarDireccion = false
                                seleccionarFechasYHora = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f),
                        ) {
                            Text(text = "Volver", color = MaterialTheme.colorScheme.onPrimary)
                        }

                        Spacer(modifier = Modifier.padding(8.dp))

                        Button(
                            onClick = {
                                val direccion = viewModel.validarDireccion(linea1 = linea1, linea2 = linea2.ifBlank { null }, ciudad = ciudad, estado = estado, pais = pais, cp = cp)
                                if(direccion != null) {
                                    if(servicio == null) {
                                        GlobalScope.launch {
                                            val nuevoServicio = viewModel.crearServicio(
                                                nombre = nombre,
                                                descripcion = descripcion.ifBlank { null },
                                                precio = precio.replace(",", ".").toDouble(),
                                                fechaInicio = fechaInicio,
                                                fechaFinal = fechaFinal.ifBlank { null },
                                                hora = hora.ifBlank { null },
                                                tipoServicio = tipoServicio!!,
                                                direccion = direccion
                                            )
                                            if (nuevoServicio != null) {
                                                withContext(Dispatchers.Main) {
                                                    navigateToServicio(nuevoServicio.id!!)
                                                }
                                            }
                                        }
                                    } else {

                                        val inicio = LocalDate.parse(fechaInicio, formatoFinal).format(formatoFromDb)
                                        var final : String? = null
                                        if(fechaFinal.isNotBlank()) {
                                            final = LocalDate.parse(fechaFinal, formatoFinal).format(formatoFromDb)
                                        }

                                        GlobalScope.launch {
                                            val servicioActualizado = viewModel.actualizarServicio(
                                                servicio = servicio.copy(
                                                    nombre = nombre,
                                                    descripcion = descripcion.ifBlank { null },
                                                    precio = precio.replace(",", ".").toDouble(),
                                                    fechaInicio = inicio,
                                                    fechaFinal = final,
                                                    hora = hora.ifBlank { null },
                                                    tipoServicio = tipoServicio!!,
                                                    direccion = direccion,
                                                    imagenes = listOf(),
                                                    resenas = listOf()
                                                )
                                            )
                                            if (servicioActualizado != null) {
                                                withContext(Dispatchers.Main) {
                                                    navigateToServicio(servicio.id!!)
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f),
                        ) {
                            Text(text = "Finalizar", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                }

            }

        }

    }
}




