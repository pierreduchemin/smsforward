package com.pierreduchemin.smsforward.data.source.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object for the tasks table.
 */
@Dao
interface GlobalModelDao {

    @Query("SELECT * FROM GlobalModel WHERE id = 1")
    fun observeGlobalModel(): LiveData<GlobalModel?>

    @Query("SELECT * FROM GlobalModel WHERE id = 1")
    fun getGlobalModel(): GlobalModel?

    @Query("SELECT COUNT(*) FROM GlobalModel")
    fun countGlobalModel(): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGlobalModel(globalModel: GlobalModel): Long

    @Update
    fun updateGlobalModel(globalModel: GlobalModel)
}