package com.pierreduchemin.smsforward.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [GlobalModel::class, ForwardModel::class], version = 4, exportSchema = false)
abstract class SMSForwardDatabase : RoomDatabase() {

    abstract fun globalModelDao(): GlobalModelDao

    abstract fun forwardModelDao(): ForwardModelDao
}