package com.pierreduchemin.smsforward.data

import androidx.lifecycle.LiveData

interface ForwardModelDataSource {

    fun observeForwardModel(): LiveData<ForwardModel?>
    fun getForwardModel(): ForwardModel?
    fun countForwardModel(): LiveData<Long>
    fun updateForwardModel(forwardModel: ForwardModel)
    fun insertForwardModel(forwardModel: ForwardModel)
    fun deleteForwardModelById(id: Long): Int
}