package com.gotravel.mobile.ui.screen

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.gotravel.gotravel.R
import com.gotravel.mobile.ui.AppBottomBar
import com.gotravel.mobile.ui.AppTopBar
import com.gotravel.mobile.ui.Screen
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Date

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    navigateToStart: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_signal_wifi_connected_no_internet_4_24),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
        )
        Button(onClick = navigateToStart) {
            Text(text = "Reintentar")
        }
    }
}

@Composable
fun LandingLoadingScreen(modifier: Modifier = Modifier) {

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Image(
            modifier = modifier.size(70.dp),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = ""
        )

    }

}

@Composable
fun AppLoadingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    elementosDeNavegacion: List<Screen>
) {

    Scaffold (
        topBar = {
            AppTopBar(
                title = stringResource(id = HomeDestination.titleRes),
                canNavigateBack = false
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = HomeDestination.route,
                navController = navController,
                items = elementosDeNavegacion
            )
        },
        modifier = modifier
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Image(
                modifier = modifier.size(70.dp),
                painter = painterResource(R.drawable.loading_img),
                contentDescription = ""
            )

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
object FechasDisponibles: SelectableDates {
    @ExperimentalMaterial3Api
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return utcTimeMillis >= calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalMaterial3Api
    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    val datePickerState = rememberDatePickerState(selectableDates = FechasDisponibles)

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
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
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
fun MyTimePickerDialog(
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {

    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {

                Column (
                    modifier = Modifier.wrapContentSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ){
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                        }
                    }

                    Text(text = "Selecciona la hora", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.padding(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        var hourDropdownExpanded by remember { mutableStateOf(false) }
                        Card (
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ){
                            Text(text = selectedHour.toString().padStart(2, '0'), modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable { hourDropdownExpanded = true })
                            DropdownMenu(expanded = hourDropdownExpanded, onDismissRequest = { hourDropdownExpanded = false }) {
                                hours.forEach { hour ->
                                    DropdownMenuItem(
                                        onClick = { selectedHour = hour; hourDropdownExpanded = false },
                                        text = { Text(text = hour.toString().padStart(2, '0')) }
                                    )
                                }
                            }
                        }

                        var minuteDropdownExpanded by remember { mutableStateOf(false) }
                        Card (
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(text = selectedMinute.toString().padStart(2, '0'), modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable { minuteDropdownExpanded = true })
                            DropdownMenu(expanded = minuteDropdownExpanded, onDismissRequest = { minuteDropdownExpanded = false }) {
                                minutes.forEach { minute ->
                                    DropdownMenuItem(
                                        onClick = { selectedMinute = minute; minuteDropdownExpanded = false },
                                        text = { Text(text = minute.toString().padStart(2, '0')) }
                                    )
                                }
                            }
                        }

                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    Button(onClick = {
                        onTimeSelected("$selectedHour:$selectedMinute")
                        onDismiss()
                    }) {
                        Text(text = "Seleccionar")
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                }

            }
        }
    }

}



@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}

