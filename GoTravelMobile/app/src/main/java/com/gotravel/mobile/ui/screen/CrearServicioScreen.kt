package com.gotravel.mobile.ui.screen

import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.CrearServicioViewModel
import com.gotravel.mobile.ui.screen.viewmodels.CrearViajeViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Date


object CrearServicioDestination : NavDestination {
    override val route = "crearServicio"
    override val titleRes = R.string.app_name
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun CrearServicioScreen(
    modifier: Modifier = Modifier,
    viewModel: CrearServicioViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    navigateToServicio: (Int) -> Unit
) {

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

        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){

            Text(
                text = "Nuevo servicio",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            var nombre by remember { mutableStateOf("") }
            var descripcion by remember { mutableStateOf("") }
            var precio by remember { mutableStateOf("") }
            var fechaInicio by remember { mutableStateOf("") }
            var fechaFinal by remember { mutableStateOf("") }
            var seleccionarFechaInicio by remember { mutableStateOf(false) }
            var seleccionarFechaFinal by remember { mutableStateOf(false) }
            var hora by remember { mutableStateOf("") }
            var seleccionarHora by remember { mutableStateOf(false) }

            val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del servicio*") },
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
                modifier = modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio del servicio*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                modifier = modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del servicio") },
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
                modifier = modifier.fillMaxWidth()
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

            if(seleccionarFechaFinal) {
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
                        Icon(painter = painterResource(id = R.drawable.baseline_access_time_24), contentDescription = "")
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

            if(seleccionarHora) {
                MyTimePickerDialog(
                    onTimeSelected = { hora = it },
                    onDismiss = { seleccionarHora = !seleccionarHora }
                )
            }

            // TODO: Dirección del servicio

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    GlobalScope.launch {
                        /*
                        TODO:
                        val servicio = viewModel.crearServicio(nombre = nombre, descripcion = descripcion, fechaInicio = fechaInicio, fechaFin = fechaFinal)
                        if(servicio != null) {
                            withContext(Dispatchers.Main) {
                                navigateToServicio(servicio.id!!)
                            }
                        }
                         */
                    }

                },
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Crear viaje")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "")
                }
            }

            Text(
                text = mensajeUi.value,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
            )

        }

    }

}




