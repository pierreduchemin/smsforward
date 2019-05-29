package com.pierreduchemin.smsforward.redirects

import com.pierreduchemin.smsforward.BasePresenter
import com.pierreduchemin.smsforward.BaseView

interface RedirectsContract {

    interface View : BaseView<Presenter> {
        fun redirectSetConfirmation(source: String, destination: String)
        fun showError(message: Int)
        fun hasPermission(permissionString: String): Boolean
        fun askPermission(permissionString: String)
    }

    interface Presenter : BasePresenter {
        fun onStart()
        fun onStop()
        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
        fun setRedirect(source: String, destination: String)
    }
}