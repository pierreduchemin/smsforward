package com.pierreduchemin.smsforward.ui.addredirect

interface AddRedirectContract {

    interface View {
        fun showError(message: Int)
        fun hasPermission(permissionString: String): Boolean
        fun askPermission(permissionsString: ArrayList<String>)
        fun pickNumber(requestCode: Int)
        fun setSource(source: String)
        fun setDestination(destination: String)
        fun setButtonState(buttonState: AddRedirectFragment.ButtonState)
        fun resetFields()
    }
}