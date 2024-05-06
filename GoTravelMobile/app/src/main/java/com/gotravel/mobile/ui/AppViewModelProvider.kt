package com.gotravel.mobile.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gotravel.mobile.GoTravelApplication
import com.gotravel.mobile.ui.screen.viewmodel.CredencialesViewModel
import com.gotravel.mobile.ui.screen.viewmodel.LandingViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            LandingViewModel(goTravelApplication().container.ApiRepository)
        }

        initializer {
            CredencialesViewModel(this.createSavedStateHandle())
        }

    }

}

fun CreationExtras.goTravelApplication() : GoTravelApplication = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GoTravelApplication)