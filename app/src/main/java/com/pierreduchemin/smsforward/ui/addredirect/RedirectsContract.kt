package com.pierreduchemin.smsforward.ui.addredirect

interface RedirectsContract {

    interface View {
        fun showError(message: Int)
        fun hasPermission(permissionString: String): Boolean
        fun askPermission(permissionString: String)
        fun pickNumber(requestCode: Int)
        fun setSource(source: String)
        fun setDestination(destination: String)
        fun setButtonState(buttonState: AddRedirectFragment.ButtonState)
        fun resetFields()
        fun showRedirectMessage(source: String, destination: String)
    }
}