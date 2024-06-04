package com.gotravel.clienteMovil.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gotravel.clienteMovil.GoTravelApplication
import com.gotravel.clienteMovil.ui.screens.LoginViewModel
import com.gotravel.clienteMovil.ui.screens.NuevoViajeViewModel
import com.gotravel.clienteMovil.ui.screens.ViajesViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            LoginViewModel()
        }

        initializer {
            ViajesViewModel(
                this.createSavedStateHandle(),
                repository = goTravelApplication().container.ApiRepository
            )
        }

        initializer {
            NuevoViajeViewModel(
                this.createSavedStateHandle(),
                repository = goTravelApplication().container.ApiRepository
            )
        }

    }

}

fun CreationExtras.goTravelApplication() : GoTravelApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GoTravelApplication)