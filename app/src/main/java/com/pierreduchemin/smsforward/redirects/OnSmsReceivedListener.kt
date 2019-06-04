package com.pierreduchemin.smsforward.redirects

interface OnSmsReceivedListener {
    fun onSmsReceived(message: String)
}
