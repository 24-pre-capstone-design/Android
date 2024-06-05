package com.capston2024.capstonapp

import android.app.Application
import com.capston2024.capstonapp.presentation.startend.StartViewModel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application(){
    val startViewModel: StartViewModel by lazy {
        StartViewModel()
    }
    override fun onCreate() {
        super.onCreate()
    }
}