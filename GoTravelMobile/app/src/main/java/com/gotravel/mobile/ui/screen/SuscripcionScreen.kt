package com.gotravel.mobile.ui.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.model.Metodopago
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.AppViewModelProvider
import com.gotravel.mobile.ui.navigation.NavDestination
import com.gotravel.mobile.ui.screen.viewmodels.SuscripcionUiState
import com.gotravel.mobile.ui.screen.viewmodels.SuscripcionViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

object SuscripcionDestination : NavDestination {
    override val route = "suscripcion"
    override val titleRes = R.string.app_name
    const val esProfesional = "esProfesional"
    val routeWithArgs = "$route/{${esProfesional}}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuscripcionScreen (
    modifier: Modifier = Modifier,
    viewModel: SuscripcionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController
){

    val esProfesional = viewModel.esProfesional

    Scaffold (
        topBar = {
            AppTopBar(
                title = stringResource(id = CrearViajeDestination.titleRes),
                canNavigateBack = true,
                navigateUp = { navController.popBackStack() }
            )
        },
        modifier = modifier
    ){ innerPadding ->

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if(esProfesional) {
                // TODO
            } else {
                SuscribirseScreen(
                    viewModel,
                    navigateToStart = {
                        navController.navigate(LandingDestination.route)
                    }
                )
            }
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuscribirseScreen(
    viewModel: SuscripcionViewModel,
    navigateToStart: () -> Unit
) {

    var mostrarMas by remember { mutableStateOf(false) }

    if(mostrarMas) {
        Dialog(onDismissRequest = { mostrarMas = false }) {
            Suscribirse(
                viewModel = viewModel,
                cerrarDialogo = { mostrarMas = !mostrarMas },
                navigateToStart = navigateToStart
            )
        }
    }

    Card (
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido al Programa para Profesionales de GoTravel!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Tienes un servicio que puede mejorar la experiencia de viaje de nuestros usuarios? " +
                        "¡Estás en el lugar correcto! Nuestro programa para profesionales está abierto a todo " +
                        "aquel que desee compartir sus recursos para que otros usuarios puedan disfrutar de ellos.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Qué puedes ofrecer?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Desde alojamiento, alquiler de vehículos, guías turísticas, hasta la oportunidad de compartir " +
                        "un viaje, ¡las posibilidades son infinitas! Si tienes un servicio que puede mejorar la experiencia" +
                        " de viaje de nuestros usuarios, queremos saber de ti.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Cómo funciona?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Por una suscripción mensual de solo 4.99€, puedes convertirte en un proveedor de servicios en nuestra plataforma. " +
                        "Esto te permitirá publicar y promocionar tus servicios a nuestra creciente comunidad de viajeros.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¿Por qué unirse?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Al unirte a nuestro programa para profesionales, no solo tendrás la oportunidad de aumentar tus ingresos, sino que también " +
                        "podrás recibir valoraciones de los clientes y mejorar continuamente tus servicios basándote en sus comentarios.",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "¡Únete a nosotros hoy y comienza tu viaje hacia el éxito!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                onClick = {
                    mostrarMas = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = "Unirse",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

}

@OptIn(DelicateCoroutinesApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Suscribirse(
    viewModel: SuscripcionViewModel,
    cerrarDialogo: () -> Unit,
    navigateToStart: () -> Unit
) {

    when (val uiState = viewModel.uiState) {
        is SuscripcionUiState.Loading -> {
            LandingLoadingScreen()
        }
        is SuscripcionUiState.Success -> {

            var addMetodopago by remember { mutableStateOf(false) }
            var metodoPagoaUsar by remember { mutableStateOf<Metodopago?>(null) }

            if(addMetodopago) {
                Dialog(onDismissRequest = { addMetodopago = false }) {
                    NuevoMetodoPago(
                        viewModel = viewModel,
                        cerrarDialogo = { addMetodopago = !addMetodopago }
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Card (
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {

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

                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Plan de profesionales", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { cerrarDialogo() }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "")
                            }
                        }

                        Spacer(modifier = Modifier.padding(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "4.99€", style = MaterialTheme.typography.titleLarge)
                        }

                        /*
                        Spacer(modifier = Modifier.padding(16.dp))

                        Text(text = "Selecciona un método de pago", style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.padding(8.dp))

                        if(uiState.metodosPago.isNotEmpty()) {
                            LazyColumn {
                                items(uiState.metodosPago) {metodo ->
                                    Row(
                                        Modifier
                                            .selectable(selected = (metodo == metodoPagoaUsar), onClick = { metodoPagoaUsar = metodo }),
                                        verticalAlignment = Alignment.CenterVertically

                                    ) {
                                        RadioButton(
                                            selected = (metodo == metodoPagoaUsar),
                                            onClick = { metodoPagoaUsar = metodo }
                                        )
                                        Text(
                                            text = when (metodo) {
                                                is Tarjetacredito -> metodo.tipo + " que acaba en " + metodo.numero.takeLast(4)
                                                is Paypal -> "Paypal: " + metodo.email
                                                else -> "Desconocido"
                                            },
                                            modifier = Modifier.padding(start = 16.dp),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(text = "No tienes métodos de pago guardados", style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.padding(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.clickable {
                                addMetodopago = true
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "")
                            Text(text = "Añadir método de pago")
                        }
                        
                        Spacer(modifier = Modifier.padding(8.dp))

                        var mensajeError by remember { mutableStateOf("") }
                        Text(text = mensajeError)
                         */

                        val context = LocalContext.current
                        Button(onClick = {
                            GlobalScope.launch {
                                viewModel.suscribirse(context)
                            }
                        }) {
                            Text(text = "Pagar con PayPal")
                        }

                    }

                }

            }

        }
        else -> ErrorScreen(navigateToStart = navigateToStart)
    }



}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun NuevoMetodoPago(
    viewModel: SuscripcionViewModel,
    cerrarDialogo: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        var fechaVencimiento by remember { mutableStateOf("") }
        var titular by remember { mutableStateOf("") }
        var numero by remember { mutableStateOf("") }
        var cvv by remember { mutableStateOf("") }

        var guardarMetodoPago by remember { mutableStateOf(false) }

        var seleccionarFechaVencimiento by remember { mutableStateOf(false) }

        val mensajeUi = viewModel.mensajeUi.observeAsState(initial = "")

        Card (
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.45f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
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
                    text = "Nuevo método de pago",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = titular,
                    onValueChange = { titular = it },
                    label = { Text("Titular de la tarjeta*") },
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
                    value = numero,
                    onValueChange = { numero = it },
                    label = { Text("Numero de la tarjeta*") },
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
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = { Text("Código de seguridad*") },
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
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = fechaVencimiento,
                    onValueChange = { fechaVencimiento = it },
                    label = { Text("Fecha de vencimiento*") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { seleccionarFechaVencimiento = !seleccionarFechaVencimiento }) {
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

                if(seleccionarFechaVencimiento) {
                    MonthYearPickerDialog(
                        onMonthYearSelected = { fechaVencimiento = it },
                        onDismiss = { seleccionarFechaVencimiento = !seleccionarFechaVencimiento }
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                var calle by remember { mutableStateOf("") }
                var ciudad by remember { mutableStateOf("") }
                var estado by remember { mutableStateOf("") }
                var cp by remember { mutableStateOf("") }
                var pais by remember { mutableStateOf("") }

                Text(
                    text = "Dirección de facturación",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = calle,
                    onValueChange = { calle = it },
                    label = { Text("Calle*") },
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
                    modifier = Modifier
                        .fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = estado,
                    onValueChange = { estado = it },
                    label = { Text("Estado/Provincia*") },
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
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextField(
                    value = cp,
                    onValueChange = { cp = it },
                    label = { Text("Código postal*") },
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
                    modifier = Modifier
                        .fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = guardarMetodoPago,
                        onCheckedChange = { guardarMetodoPago = it },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary)
                    )

                    Text(
                        text = "Guardar metodo de pago",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = mensajeUi.value,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {

                        val dirFacturacion = viewModel.validarDireccion(calle = calle, ciudad = ciudad, estado = estado, pais = pais, cp = cp)

                        if(dirFacturacion != null) {

                            val tarjeta = viewModel.validarMetodopago(numero, titular, fechaVencimiento, cvv, dirFacturacion = dirFacturacion)
                            if(tarjeta != null) {
                                if(guardarMetodoPago) {
                                    GlobalScope.launch {
                                        viewModel.guardarMetodopago(tarjeta)
                                    }
                                }
                                cerrarDialogo()
                            }
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar método de pago")
                }

            }

        }

    }

}

@Composable
fun MonthYearPickerDialog(
    onMonthYearSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }

    val months = (1..12).toList()
    val years = (currentYear..2099).toList()

    var showMonthDropdown by remember { mutableStateOf(false) }
    var showYearDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                val selectedDate = "${selectedMonth.toString().padStart(2, '0')}/${selectedYear.toString().substring(2)}"
                onMonthYearSelected(selectedDate)
                onDismiss()
            }) {
                Text(text = "Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancelar")
            }
        },
        title = { Text(text = "Seleccione el mes y el año", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        text = {
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Mes:")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                    ) {
                        Text(
                            text = selectedMonth.toString(),
                            Modifier
                                .clickable { showMonthDropdown = true }
                                .padding(8.dp)
                                .width(128.dp), textAlign = TextAlign.Center
                        )
                        DropdownMenu(expanded = showMonthDropdown, onDismissRequest = { showMonthDropdown = false }) {
                            months.forEach { month ->
                                DropdownMenuItem(
                                    text = {Text(text = month.toString())},
                                    onClick = {
                                        selectedMonth = month
                                        showMonthDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Año:")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                    ) {
                        Text(
                            text = selectedYear.toString(),
                            Modifier
                                .clickable { showYearDropdown = true }
                                .padding(8.dp)
                                .width(128.dp), textAlign = TextAlign.Center
                        )
                        DropdownMenu(
                            expanded = showYearDropdown, onDismissRequest = { showYearDropdown = false },
                        ) {
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = {Text(text = year.toString())},
                                    onClick = {
                                        selectedYear = year
                                        showYearDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}




