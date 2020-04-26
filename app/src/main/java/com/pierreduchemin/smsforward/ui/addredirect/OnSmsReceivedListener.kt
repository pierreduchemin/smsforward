package com.pierreduchemin.smsforward.ui.addredirect

interface OnSmsReceivedListener {
    fun onSmsReceived(source: String, message: String)
}
