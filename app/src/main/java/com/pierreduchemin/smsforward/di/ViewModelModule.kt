package com.pierreduchemin.smsforward.di

import android.content.Context
import androidx.room.Room
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.data.source.database.SMSForwardDatabase
import toothpick.config.Module

class ViewModelModule(context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)

        val smsForwardRoomDatabase = Room.databaseBuilder(
            context.applicationContext,
            SMSForwardDatabase::class.java,
            "smsforward_database")
            .fallbackToDestructiveMigration()
            .build()
        bind(SMSForwardDatabase::class.java).toInstance(smsForwardRoomDatabase)

        val forwardModelRepository = ForwardModelRepository(smsForwardRoomDatabase.forwardModelDao())
        bind(ForwardModelRepository::class.java).toInstance(forwardModelRepository)

        val globalModelRepository = GlobalModelRepository(smsForwardRoomDatabase.globalModelDao())
        bind(GlobalModelRepository::class.java).toInstance(globalModelRepository)
    }
}