package com.pierreduchemin.smsforward.ui.addredirect

import com.pierreduchemin.smsforward.ui.ErrorContract
import com.pierreduchemin.smsforward.ui.PermissionSubscriber

interface AddRedirectSubscriber: ErrorContract, PermissionSubscriber {
    fun pickNumber(requestCode: Int)
    fun setSource(source: String)
    fun setDestination(destination: String)
    fun setButtonState(buttonState: AddRedirectFragment.ButtonState)
    fun resetFields()
}