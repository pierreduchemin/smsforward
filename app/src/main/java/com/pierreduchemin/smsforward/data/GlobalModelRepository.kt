package com.pierreduchemin.smsforward.data

import com.pierreduchemin.smsforward.data.source.database.GlobalModelDao

class GlobalModelRepository(private val globalModelDao: GlobalModelDao) : GlobalModelDataSource {

    override fun observeGlobalModel() =
        globalModelDao.observeGlobalModel()

    override fun getGlobalModel() =
        globalModelDao.getGlobalModel()

    override fun countGlobalModel() =
        globalModelDao.countGlobalModel()

    override fun updateGlobalModel(globalModel: GlobalModel) {
        globalModelDao.updateGlobalModel(globalModel)
    }

    override fun insertGlobalModel(globalModel: GlobalModel) {
        globalModelDao.insertGlobalModel(globalModel)
    }
}