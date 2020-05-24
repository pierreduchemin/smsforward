package com.pierreduchemin.smsforward

import android.app.Application
import com.pierreduchemin.smsforward.di.AppComponent
import com.pierreduchemin.smsforward.di.AppModule
import com.pierreduchemin.smsforward.di.DaggerAppComponent

class App : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}