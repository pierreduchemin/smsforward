package com.pierreduchemin.smsforward.presentation.addredirect

import com.pierreduchemin.smsforward.presentation.ErrorContract
import com.pierreduchemin.smsforward.presentation.PermissionSubscriber

interface AddRedirectSubscriber: ErrorContract, PermissionSubscriber {
    fun pickNumber(requestCode: Int)
    fun setSource(source: String)
    fun setDestination(destination: String)
    fun setButtonState(buttonState: AddRedirectFragment.ButtonState)
    fun resetFields()
}