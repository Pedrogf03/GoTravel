package com.gotravel.clienteMovil.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.clienteMovil.R
import com.gotravel.clienteMovil.data.Viaje
import com.gotravel.clienteMovil.ui.AppViewModelProvider
import com.gotravel.clienteMovil.ui.GoTravelBottomBar
import com.gotravel.clienteMovil.ui.GoTravelTopAppBar
import com.gotravel.clienteMovil.ui.navigation.NavigationDestination
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

object NuevoViajeDestination : NavigationDestination {
    override val route = "nuevoViaje"
    override val titleRes = R.string.nuevo_viaje
    const val usuarioId = "usuarioId"
    val routeWithArgs = "$route/{$usuarioId}"
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NuevoViajeScreen (
    modifier: Modifier = Modifier,
    viewModel: NuevoViajeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    navigateToHome: (Int) -> Unit
) {
    val nuevoViajeUiState = viewModel.nuevoViajeUiState
    val retryAction = viewModel::findUsuarioById

    when (nuevoViajeUiState) {
        is NuevoViajeUiState.Loading -> {
            LoadingScreen(
                modifier = modifier
                    .fillMaxSize()
            )
        }
        is NuevoViajeUiState.Success -> {

            Scaffold(
                topBar = {
                    GoTravelTopAppBar(
                        title = stringResource(NuevoViajeDestination.titleRes),
                        canNavigateBack = false
                    )
                },
                bottomBar = {
                    GoTravelBottomBar(
                        currentRoute = ViajesDestination.route,
                        navController = navController,
                        usuarioId = nuevoViajeUiState.usuario.id
                    )
                }
                , modifier = modifier
            ) { innerPadding ->

                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Card(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {

                        Column(
                            modifier = modifier
                                .wrapContentSize(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            var nombre by remember { mutableStateOf("") }
                            var descripcion by remember { mutableStateOf("") }
                            var fechaInicio by remember { mutableStateOf("") }
                            var fechaFinal by remember { mutableStateOf("") }
                            var enabled by remember { mutableStateOf(true) }
                            var mensajeError by remember { mutableStateOf("") }

                            Text(
                                text = "Nuevo viaje",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                shape = RoundedCornerShape(16.dp),
                                label = { Text(stringResource(R.string.nombre_del_viaje)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                            )

                            OutlinedTextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                shape = RoundedCornerShape(16.dp),
                                label = { Text(stringResource(R.string.descripcion_del_viaje)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                            )

                            Text(
                                text = "Fecha de inicio",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                            fechaInicio = myDatePickerDialog()

                            Text(
                                text = "Fecha de final",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                            fechaFinal = myDatePickerDialog()

                            if (fechaInicio != "Selecciona una fecha" && fechaFinal != "Selecciona una fecha") {
                                val inicio = LocalDate.parse(fechaInicio)
                                val final = LocalDate.parse(fechaFinal)

                                if (final.isBefore(inicio)) {
                                    mensajeError = "La fecha de final no puede ser anterior a la fecha de inicio"
                                    enabled = false
                                } else {
                                    enabled = true
                                }
                            }
                            
                            Text(text = mensajeError)

                            Button(onClick = {
                                if(nombre.isBlank() || nombre.isEmpty() || fechaInicio == "Selecciona una fecha") {
                                    mensajeError = "Rellena todos los datos necesarios"
                                } else {
                                    if(fechaFinal == "Selecciona una fecha") {
                                        GlobalScope.launch {
                                            val guardado = viewModel.saveViaje(Viaje(0, nombre, descripcion, fechaInicio, null, 0.0))
                                            if(guardado) {
                                                withContext(Dispatchers.Main) {
                                                    navigateToHome(nuevoViajeUiState.usuario.id)
                                                }
                                            } else {
                                                mensajeError = "No se ha podido crear el viaje"
                                            }
                                        }
                                    } else {
                                        GlobalScope.launch {
                                            val guardado = viewModel.saveViaje(Viaje(0, nombre, descripcion, fechaInicio, fechaFinal, 0.0))
                                            if(guardado) {
                                                withContext(Dispatchers.Main) {
                                                    navigateToHome(nuevoViajeUiState.usuario.id)
                                                }
                                            } else {
                                                mensajeError = "No se ha podido crear el viaje"
                                            }
                                        }
                                    }
                                }
                            },
                                modifier = Modifier.padding(bottom = 8.dp),
                                enabled = enabled
                            ) {
                                Text(text = "Crear viaje")
                            }

                        }
                    }
                }

            }

        }
        else -> {
            ErrorScreen(
                retryAction = retryAction
            )
        }

    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun myDatePickerDialog() : String {
    var date by remember {
        mutableStateOf("Selecciona una fecha")
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    Box(contentAlignment = Alignment.Center) {
        Button(onClick = { showDatePicker = true }) {
            Text(text = date)
        }
    }

    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { date = it },
            onDismiss = { showDatePicker = false }
        )
    }

    return date

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancelar")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(Date(millis))
}

