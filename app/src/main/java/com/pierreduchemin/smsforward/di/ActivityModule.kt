package com.pierreduchemin.smsforward.di

import android.content.Context
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import toothpick.config.Module

class ActivityModule(context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)

        val forwardModelRepository = ForwardModelRepository(context)
        bind(ForwardModelRepository::class.java).toInstance(forwardModelRepository)
    }
}