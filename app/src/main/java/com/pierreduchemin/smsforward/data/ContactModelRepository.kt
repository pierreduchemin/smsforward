package com.pierreduchemin.smsforward.data

import android.content.Context
import android.net.Uri
import com.pierreduchemin.smsforward.data.source.aosp.AospSource

object ContactModelRepository : ContactModelDataSource {

    override fun pickContact(context: Context): ContactModel? {
        return AospSource.getContact(context)
    }
}