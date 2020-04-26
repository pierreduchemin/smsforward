package com.pierreduchemin.smsforward.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pierreduchemin.smsforward.data.ForwardModel
import javax.inject.Singleton

@Singleton
@Database(entities = [ForwardModel::class], version = 1, exportSchema = false)
abstract class SMSForwardDatabase : RoomDatabase() {

    abstract fun forwardModelDao(): ForwardModelDao
}