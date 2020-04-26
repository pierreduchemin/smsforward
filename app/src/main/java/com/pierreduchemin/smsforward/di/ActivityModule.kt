package com.pierreduchemin.smsforward.di

import android.content.Context
import androidx.room.Room
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.source.database.SMSForwardDatabase
import toothpick.config.Module

class ActivityModule(context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)

        val smsForwardRoomDatabase = Room.databaseBuilder(
            context.applicationContext,
            SMSForwardDatabase::class.java,
            "smsforward_database"
        ).build()
        bind(SMSForwardDatabase::class.java).toInstance(smsForwardRoomDatabase)

        val repository = ForwardModelRepository(smsForwardRoomDatabase.forwardModelDao())
        bind(ForwardModelRepository::class.java).toInstance(repository)
    }
}