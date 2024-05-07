package com.pierreduchemin.smsforward.di

import android.content.Context
import androidx.room.Room
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.data.source.database.SMSForwardDatabase
import com.pierreduchemin.smsforward.utils.RedirectionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    private lateinit var smsForwardDatabase: SMSForwardDatabase

    @Singleton
    @Provides
    fun provideSmsForwardDatabase(@ApplicationContext appContext: Context): SMSForwardDatabase {
        if (!::smsForwardDatabase.isInitialized) {
            smsForwardDatabase = Room.databaseBuilder(
                appContext,
                SMSForwardDatabase::class.java,
                "smsforward_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return smsForwardDatabase
    }

    @Provides
    @Singleton
    fun provideForwardModelRepository(@ApplicationContext appContext: Context) =
        ForwardModelRepository(provideSmsForwardDatabase(appContext).forwardModelDao())

    @Provides
    @Singleton
    fun provideGlobalModelRepository(@ApplicationContext appContext: Context) =
        GlobalModelRepository(provideSmsForwardDatabase(appContext).globalModelDao())

    @Provides
    @Singleton
    fun provideRedirectionManager(@ApplicationContext appContext: Context) =
        RedirectionManager(provideGlobalModelRepository(appContext), provideForwardModelRepository(appContext))

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext appContext: Context) = appContext
}