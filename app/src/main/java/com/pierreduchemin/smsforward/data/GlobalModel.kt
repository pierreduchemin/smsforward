package com.pierreduchemin.smsforward.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GlobalModel(

    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Long,

    @ColumnInfo
    var activated: Boolean
)