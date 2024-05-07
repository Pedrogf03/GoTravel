package com.gotravel.mobile.ui.screen.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotravel.gotravel.R
import com.gotravel.mobile.data.AppRepository
import kotlinx.coroutines.launch
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.Properties

sealed interface LandingUiState {
    data class Success(val imagen: ImageBitmap) : LandingUiState
    data class Error(val imagen: Int) : LandingUiState
    object Loading : LandingUiState
}

class LandingViewModel(
    private val repository: AppRepository
) : ViewModel() {

    var uiState: LandingUiState by mutableStateOf(LandingUiState.Loading)
        private set

    init {
        getImagen()
    }

    fun getImagen() {
        viewModelScope.launch {
            try {
                uiState = LandingUiState.Success(repository.getImagen())
            } catch (e: IOException) {
                uiState =
                    LandingUiState.Error(R.drawable.resource_default)
            }
        }
    }

    fun cambiarIp(context: Context, nuevaIp: String) {
        val propiedades = Properties()

        val file = context.getFileStreamPath("client.properties")
        if (!file.exists()) {
            context.openFileOutput("client.properties", Context.MODE_PRIVATE).use { outputStream ->
                propiedades.setProperty("IP", "80.31.21.94")
                propiedades.setProperty("PUERTO", "8484")
                propiedades.store(outputStream, null)
            }
        }

        context.openFileInput("client.properties").use { inputStream ->
            propiedades.load(inputStream)
        }

        propiedades.setProperty("IP", nuevaIp)

        context.openFileOutput("client.properties", Context.MODE_PRIVATE).use { outputStream ->
            propiedades.store(outputStream, null)
        }
    }



}