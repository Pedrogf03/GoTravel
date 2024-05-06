package com.gotravel.mobile.ui.screen.viewmodel

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotravel.mobile.data.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface LandingUiState {
    data class Success(val imagen: ImageBitmap) : LandingUiState
    data class Error(val imagen: String) : LandingUiState
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
                    LandingUiState.Error("https://viajes.nationalgeographic.com.es/medio/2019/03/20/empire-state-building_ecb995bd_800x800.jpg")
            }
        }
    }

    private fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap {
        return BitmapFactory.decodeByteArray(encodedImageData, 0, encodedImageData.size).asImageBitmap()
    }

}