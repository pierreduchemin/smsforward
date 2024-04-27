package com.pierreduchemin.smsforward.data

import android.content.Context

interface ContactModelDataSource {

    fun pickContact(context: Context): ContactModel?
}