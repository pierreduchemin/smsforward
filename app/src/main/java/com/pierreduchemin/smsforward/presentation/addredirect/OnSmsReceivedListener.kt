package com.pierreduchemin.smsforward.presentation.addredirect

interface OnSmsReceivedListener {
    fun onSmsReceived(phoneNumberFrom: String, message: String)
}
