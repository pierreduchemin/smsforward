package com.pierreduchemin.smsforward.data.source.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pierreduchemin.smsforward.data.GlobalModel

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
    fun insertGlobalModel(GlobalModel: GlobalModel): Long

    @Update
    fun updateGlobalModel(GlobalModel: GlobalModel)
}