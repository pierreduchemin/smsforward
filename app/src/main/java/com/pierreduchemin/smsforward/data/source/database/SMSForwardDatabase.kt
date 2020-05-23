package com.pierreduchemin.smsforward.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.GlobalModel
import javax.inject.Singleton


@Singleton
@Database(entities = [GlobalModel::class, ForwardModel::class], version = 2, exportSchema = false)
abstract class SMSForwardDatabase : RoomDatabase() {

    abstract fun globalModelDao(): GlobalModelDao
    
    abstract fun forwardModelDao(): ForwardModelDao
}