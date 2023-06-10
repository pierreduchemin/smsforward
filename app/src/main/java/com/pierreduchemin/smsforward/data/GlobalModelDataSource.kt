package com.pierreduchemin.smsforward.data

import androidx.lifecycle.LiveData
import com.pierreduchemin.smsforward.data.source.database.GlobalModel

interface GlobalModelDataSource {

    fun observeGlobalModel(): LiveData<GlobalModel?>
    fun getGlobalModel(): GlobalModel?
    fun countGlobalModel(): LiveData<Long>
    fun updateGlobalModel(globalModel: GlobalModel)
    fun insertGlobalModel(globalModel: GlobalModel)
}