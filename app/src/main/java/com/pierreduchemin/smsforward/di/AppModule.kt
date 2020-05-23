package com.pierreduchemin.smsforward.di

import androidx.room.Room
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.data.source.database.SMSForwardDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: App) {

    private val smsForwardDatabase: SMSForwardDatabase = Room.databaseBuilder(
        app,
        SMSForwardDatabase::class.java,
        "smsforward_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideApp() = app

    @Provides
    @Singleton
    fun provideSmsForwardRoomDatabase(): SMSForwardDatabase {
        return smsForwardDatabase
    }

    @Provides
    @Singleton
    fun provideForwardModelRepository() = ForwardModelRepository(smsForwardDatabase.forwardModelDao())

    @Provides
    @Singleton
    fun provideGlobalModelRepository() = GlobalModelRepository(smsForwardDatabase.globalModelDao())
}