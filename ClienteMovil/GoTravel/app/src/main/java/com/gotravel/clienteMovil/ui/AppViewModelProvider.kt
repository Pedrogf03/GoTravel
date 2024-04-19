package com.gotravel.clienteMovil.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gotravel.clienteMovil.GoTravelApplication
import com.gotravel.clienteMovil.ui.screens.LoginViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            LoginViewModel()
        }

    }

}

fun CreationExtras.goTravelApplication() : GoTravelApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GoTravelApplication)