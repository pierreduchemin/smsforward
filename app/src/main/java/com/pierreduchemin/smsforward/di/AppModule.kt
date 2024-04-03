package com.pierreduchemin.smsforward.di

import android.content.Context
import androidx.room.Room
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.data.source.database.SMSForwardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideSmsForwardRoomDatabase(@ApplicationContext appContext: Context): SMSForwardDatabase =
        Room.databaseBuilder(appContext, SMSForwardDatabase::class.java, "smsforward_database")
            .build()

    @Provides
    @Singleton
    fun provideForwardModelRepository(smsForwardDatabase: SMSForwardDatabase) =
        ForwardModelRepository(smsForwardDatabase.forwardModelDao())

    @Provides
    @Singleton
    fun provideGlobalModelRepository(smsForwardDatabase: SMSForwardDatabase) =
        GlobalModelRepository(smsForwardDatabase.globalModelDao())
}