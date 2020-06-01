package com.pierreduchemin.smsforward.data

import androidx.lifecycle.LiveData

interface ForwardModelDataSource {

    fun observeForwardModels(): LiveData<List<ForwardModel>>
    fun getForwardModels(): List<ForwardModel>
    fun countForwardModel(): LiveData<Long>
    fun countSameForwardModel(from: String, to: String): Long
    fun updateForwardModel(forwardModel: ForwardModel)
    fun insertForwardModel(forwardModel: ForwardModel): Long
    fun deleteForwardModelById(id: Long): Int
}