package com.pierreduchemin.smsforward.ui.addredirect

import com.pierreduchemin.smsforward.data.ForwardModel

interface OnSmsReceivedListener {
    fun onSmsReceived(forwardModel: ForwardModel, message: String)
}
