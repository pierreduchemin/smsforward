package com.pierreduchemin.smsforward.data

import com.pierreduchemin.smsforward.data.source.database.ForwardModelDao

class ForwardModelRepository(private val forwardModelDao: ForwardModelDao) :
    ForwardModelDataSource {

    override fun observeForwardModels() =
        forwardModelDao.observeForwardModels()

    override fun getForwardModels() =
        forwardModelDao.getForwardModels()

    override fun countForwardModel() =
        forwardModelDao.countForwardModel()

    override fun countSameForwardModel(from: String, to: String) =
        forwardModelDao.countSameForwardModel(from, to)

    override fun updateForwardModel(forwardModel: ForwardModel) {
        forwardModelDao.updateForwardModel(forwardModel)
    }

    override fun insertForwardModel(forwardModel: ForwardModel) =
        forwardModelDao.insertForwardModel(forwardModel)

    override fun deleteForwardModelById(id: Long): Int = forwardModelDao.deleteForwardModelById(id)
}