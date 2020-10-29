package com.pierreduchemin.smsforward.ui.addredirect

interface OnSmsReceivedListener {
    fun onSmsReceived(phoneNumberFrom: String, message: String)
}
