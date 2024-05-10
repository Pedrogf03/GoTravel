package com.gotravel.mobile.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gotravel.mobile.GoTravelApplication
import com.gotravel.mobile.ui.screen.viewmodels.CrearViajeViewModel
import com.gotravel.mobile.ui.screen.viewmodels.CredencialesViewModel
import com.gotravel.mobile.ui.screen.viewmodels.HomeViewModel
import com.gotravel.mobile.ui.screen.viewmodels.LandingViewModel
import com.gotravel.mobile.ui.screen.viewmodels.PerfilViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ViajesViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            LandingViewModel(goTravelApplication().container.ApiRepository)
        }

        initializer {
            CredencialesViewModel(this.createSavedStateHandle())
        }

        initializer {
            HomeViewModel(goTravelApplication().container.ApiRepository)
        }

        initializer {
            ViajesViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            PerfilViewModel()
        }

        initializer {
            CrearViajeViewModel()
        }

    }

}

fun CreationExtras.goTravelApplication() : GoTravelApplication = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GoTravelApplication)