package com.pierreduchemin.smsforward

import android.app.Application
import com.pierreduchemin.smsforward.di.AppModule
import toothpick.ktp.KTP

class App : Application() {

    companion object {
        const val APPSCOPE = "appscope"
    }

    override fun onCreate() {
        super.onCreate()

        KTP.openRootScope()
            .openSubScope(APPSCOPE)
            .installModules(AppModule(this))
            .inject(this)
    }
}