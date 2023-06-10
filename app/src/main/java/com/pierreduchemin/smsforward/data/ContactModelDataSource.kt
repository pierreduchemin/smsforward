package com.pierreduchemin.smsforward.data

import android.content.Context
import android.net.Uri

interface ContactModelDataSource {

    fun pickContact(context: Context): ContactModel?
}