package com.pierreduchemin.smsforward.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ForwardModel(

    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Long,

    @ColumnInfo
    val from: String,

    @ColumnInfo
    val to: String,

    @ColumnInfo
    val active: Boolean
)