package com.example.myfinance.data

import android.app.Application
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.FirebaseApp

class MyFinanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Initialize Firebase
        FirebaseApp.initializeApp(this)

    }
}