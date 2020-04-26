package com.pierreduchemin.smsforward.data

import com.pierreduchemin.smsforward.data.source.database.ForwardModelDao

class ForwardModelRepository(private val forwardModelDao: ForwardModelDao) :
    ForwardModelDataSource {

    override fun observeForwardModel() =
        forwardModelDao.observeForwardModel()

    override fun getForwardModel() =
        forwardModelDao.getForwardModel()

    override fun countForwardModel() =
        forwardModelDao.countForwardModel()

    override fun updateForwardModel(forwardModel: ForwardModel) {
        forwardModelDao.updateForwardModel(forwardModel)
    }

    override fun insertForwardModel(forwardModel: ForwardModel) {
        forwardModelDao.insertForwardModel(forwardModel)
    }

    override fun deleteForwardModelById(id: Long): Int = forwardModelDao.deleteForwardModelById(id)
}