package com.pierreduchemin.smsforward.data

import androidx.lifecycle.LiveData

interface GlobalModelDataSource {

    fun observeGlobalModel(): LiveData<GlobalModel?>
    fun getGlobalModel(): GlobalModel?
    fun countGlobalModel(): LiveData<Long>
    fun updateGlobalModel(globalModel: GlobalModel)
    fun insertGlobalModel(globalModel: GlobalModel)
}