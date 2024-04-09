package com.gotravel.clienteMovil

import android.app.Application
import com.gotravel.clienteMovil.data.AppContainer
import com.gotravel.clienteMovil.data.DefaultAppContainer

class GoTravelApplication : Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}