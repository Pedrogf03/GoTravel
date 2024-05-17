package com.gotravel.mobile.ui.screen.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gotravel.mobile.ui.screen.SuscripcionDestination

class SuscripcionViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val esProfesional: Boolean = checkNotNull(savedStateHandle[SuscripcionDestination.esProfesional])




}