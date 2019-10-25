package com.pierreduchemin.smsforward.redirects

import com.pierreduchemin.smsforward.BasePresenter
import com.pierreduchemin.smsforward.BaseView

interface RedirectsContract {

    interface View : BaseView<Presenter> {
        fun showError(message: Int)
        fun hasPermission(permissionString: String): Boolean
        fun askPermission(permissionString: String)
        fun pickNumber(requestCode: Int)
        fun setSource(source: String)
        fun setDestination(destination: String)
        fun setButtonState(buttonState: RedirectsFragment.ButtonState)
        fun resetFields()
        fun showRedirectMessage(source: String, destination: String)
    }

    interface Presenter : BasePresenter {
        fun onButtonClicked(source: String, destination: String)
        fun onNumberPicked()
        fun onSourceSet(source: String?)
        fun onDestinationSet(destination: String?)
        fun onViewCreated()
    }
}