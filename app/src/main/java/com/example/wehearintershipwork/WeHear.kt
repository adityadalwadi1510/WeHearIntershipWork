package com.example.wehearintershipwork

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeHear:Application() {
    override fun onCreate() {
        super.onCreate()
        WeHear.appContext=applicationContext
    }
    companion object{
        lateinit  var appContext: Context
    }
}