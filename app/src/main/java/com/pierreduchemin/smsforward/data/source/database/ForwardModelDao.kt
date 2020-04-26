/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pierreduchemin.smsforward.data.source.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pierreduchemin.smsforward.data.ForwardModel

/**
 * Data Access Object for the tasks table.
 */
@Dao
interface ForwardModelDao {

    @Query("SELECT * FROM ForwardModel WHERE id = 1")
    fun observeForwardModel(): LiveData<ForwardModel?>

    @Query("SELECT * FROM ForwardModel WHERE id = 1")
    fun getForwardModel(): ForwardModel?

    @Query("SELECT COUNT(*) FROM ForwardModel")
    fun countForwardModel(): LiveData<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForwardModel(forwardModel: ForwardModel): Long

    @Update
    fun updateForwardModel(forwardModel: ForwardModel)

    @Query("DELETE FROM ForwardModel WHERE id = :id")
    fun deleteForwardModelById(id: Long): Int
}