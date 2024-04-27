package com.pierreduchemin.smsforward.data

import android.content.Context
import com.pierreduchemin.smsforward.data.source.aosp.AospSource

object ContactModelRepository : ContactModelDataSource {

    override fun pickContact(context: Context): ContactModel? {
        return AospSource.getContact(context)
    }
}