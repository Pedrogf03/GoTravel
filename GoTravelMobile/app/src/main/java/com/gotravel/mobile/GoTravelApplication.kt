package com.gotravel.mobile

import android.app.Application
import com.gotravel.mobile.data.AppContainer
import com.gotravel.mobile.data.DefaultAppContainer

class GoTravelApplication : Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

}