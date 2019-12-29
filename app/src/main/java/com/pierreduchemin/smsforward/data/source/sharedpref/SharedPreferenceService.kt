package com.pierreduchemin.smsforward.data.source.sharedpref


import android.content.Context
import android.os.Build
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelDataSource
import com.securepreferences.SecurePreferences

const val KEY_FM_ID = "KEY_FM_ID"
const val KEY_FM_FROM = "KEY_FM_FROM"
const val KEY_FM_TO = "KEY_FM_TO"
const val KEY_FM_ACTIVATED = "KEY_FM_ACTIVATED"


class SharedPreferenceService(
    context: Context
) : ForwardModelDataSource {

    private val sp = SecurePreferences(context, Build.FINGERPRINT, null)

    override fun getForwardModel(): ForwardModel? {
        val id = sp.getLong(KEY_FM_ID, -1L)
        if (id == -1L) {
            return null
        }

        val from = sp.getString(KEY_FM_FROM, "")!!
        val to = sp.getString(KEY_FM_TO, "")!!
        val activated = sp.getBoolean(KEY_FM_ACTIVATED, false)
        return ForwardModel(id, from, to, activated)
    }

    override fun countForwardModel(): Long {
        return if (sp.getLong(KEY_FM_ID, -1L) == -1L) 1 else 0
    }

    override fun insertForwardModel(forwardModel: ForwardModel) {
        sp.edit()
            .putLong(KEY_FM_ID, forwardModel.id)
            .putString(KEY_FM_FROM, forwardModel.from)
            .putString(KEY_FM_TO, forwardModel.to)
            .putBoolean(KEY_FM_ACTIVATED, forwardModel.activated)
            .apply()
    }

    override fun deleteForwardModelById(id: Long): Int {
        sp.edit().clear().apply()
        return 0
    }
}