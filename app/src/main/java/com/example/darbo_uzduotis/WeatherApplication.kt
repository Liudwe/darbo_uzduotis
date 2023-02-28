package com.example.darbo_uzduotis

import android.app.Application
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

object GlobalData {
    var temperatureScale = "Celsius"
}

@HiltAndroidApp
class WeatherApplication: Application() {
    //var globalDataTemperature: String? = null

    override fun onCreate() {
        super.onCreate()
        //globalDataTemperature = "Celsius"
        Log.d("TAG", "Application started running!")
    }
}