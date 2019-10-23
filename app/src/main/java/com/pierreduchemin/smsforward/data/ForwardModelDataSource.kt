package com.pierreduchemin.smsforward.data

interface ForwardModelDataSource {

    fun getForwardModel(): ForwardModel?
    fun countForwardModel(): Long
    fun insertForwardModel(forwardModel: ForwardModel)
    fun deleteForwardModelById(id: Long): Int
}