package com.pierreduchemin.smsforward.di

import android.app.Application
import android.content.Context
import toothpick.config.Module

class AppModule(application: Application) : Module() {

    init {
        bind(Application::class.java).toInstance(application)

        val context = application.applicationContext
        bind(Context::class.java).toInstance(context)
    }
}