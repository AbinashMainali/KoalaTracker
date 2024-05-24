package com.example.csc202assignment

import android.app.Application

// Application sub class allows you to access lifecycle information about the application itself.

class KoalaApplication: Application() {

    //Application.onCreate()is called by the system when your application is first loaded into memory.
    override fun onCreate() {
        super.onCreate()
        KoalaRepository.initialize(this)
    }
}