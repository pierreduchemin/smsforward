package com.pierreduchemin.smsforward.redirects

interface OnSmsReceivedListener {
    fun onSmsReceived(source: String, message: String)
}
