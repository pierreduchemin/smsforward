package com.pierreduchemin.smsforward.data

import android.content.Context
import com.pierreduchemin.smsforward.data.source.sharedpref.SharedPreferenceService

class ForwardModelRepository(
    context: Context
) : ForwardModelDataSource {

    private val sharedPreferenceService = SharedPreferenceService(context)

    override fun getForwardModel(): ForwardModel? {
        return sharedPreferenceService.getForwardModel()
    }

    override fun countForwardModel(): Long {
        return sharedPreferenceService.countForwardModel()
    }

    override fun insertForwardModel(forwardModel: ForwardModel) {
        return sharedPreferenceService.insertForwardModel(forwardModel)
    }

    override fun deleteForwardModelById(id: Long): Int {
        return sharedPreferenceService.deleteForwardModelById(id)
    }
}