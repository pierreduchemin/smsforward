package com.pierreduchemin.smsforward.redirects

import com.pierreduchemin.smsforward.BasePresenter
import com.pierreduchemin.smsforward.BaseView

interface RedirectsContract {

    interface View : BaseView<Presenter> {
        fun redirectSetConfirmation(source: String, destination: String)
        fun showError(message: Int)
        fun hasPermission(permissionString: String): Boolean
        fun askPermission(permissionString: String)
        fun onPickNumber(requestCode: Int)
    }

    interface Presenter : BasePresenter {
        fun onStartListening()
        fun onStopListening()
        fun setRedirect(source: String, destination: String)
    }
}