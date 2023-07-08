package com.pierreduchemin.smsforward.data.source.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GlobalModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo
    var activated: Boolean = false,

    @ColumnInfo
    var advancedMode: Boolean = false,
)