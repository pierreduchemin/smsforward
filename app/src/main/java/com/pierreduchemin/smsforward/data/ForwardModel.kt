package com.pierreduchemin.smsforward.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ForwardModel(

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    @ColumnInfo
    var from: String = "",

    @ColumnInfo
    var to: String = "",

    @ColumnInfo
    var vfrom: String = "",

    @ColumnInfo
    var vto: String = ""
)