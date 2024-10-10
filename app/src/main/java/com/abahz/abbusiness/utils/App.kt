package com.abahz.abbusiness.utils

import android.app.Application
import com.google.firebase.FirebaseApp

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}