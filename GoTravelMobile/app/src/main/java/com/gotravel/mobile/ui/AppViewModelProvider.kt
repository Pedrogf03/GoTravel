package com.gotravel.mobile.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gotravel.mobile.GoTravelApplication
import com.gotravel.mobile.ui.screen.viewmodels.BuscarServiciosViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ChatViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ChatsViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ContratacionesViewModel
import com.gotravel.mobile.ui.screen.viewmodels.CrearServicioViewModel
import com.gotravel.mobile.ui.screen.viewmodels.CrearViajeViewModel
import com.gotravel.mobile.ui.screen.viewmodels.CredencialesViewModel
import com.gotravel.mobile.ui.screen.viewmodels.HomeViewModel
import com.gotravel.mobile.ui.screen.viewmodels.LandingViewModel
import com.gotravel.mobile.ui.screen.viewmodels.PerfilViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ServicioViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ServiciosViewModel
import com.gotravel.mobile.ui.screen.viewmodels.SuscripcionViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ViajeViewModel
import com.gotravel.mobile.ui.screen.viewmodels.ViajesViewModel

object AppViewModelProvider {

    @RequiresApi(Build.VERSION_CODES.O)
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
            ViajeViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            PerfilViewModel()
        }

        initializer {
            CrearViajeViewModel()
        }

        initializer {
            SuscripcionViewModel(this.createSavedStateHandle())
        }

        initializer {
            CrearServicioViewModel()
        }

        initializer {
            ServiciosViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            ServicioViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            BuscarServiciosViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            ContratacionesViewModel(
                this.createSavedStateHandle()
            )
        }

        initializer {
            ChatsViewModel()
        }

        initializer {
            ChatViewModel(
                this.createSavedStateHandle()
            )
        }

    }

}

fun CreationExtras.goTravelApplication() : GoTravelApplication = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GoTravelApplication)