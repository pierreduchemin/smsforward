package com.pierreduchemin.smsforward.redirects

import android.Manifest
import android.app.Activity
import android.telephony.SmsManager
import android.util.Log
import com.pierreduchemin.smsforward.R
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher

class RedirectsPresenter(
    private val activity: Activity,
//    val redirectsRepository: RedirectsRepository,
    private val view: RedirectsContract.View
) : RedirectsContract.Presenter {

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    init {
        view.presenter = this
    }

    override fun start() {

    }

    override fun setRedirect(source: String, destination: String) {
        if (source.isEmpty()) {
            view.showError(R.string.redirects_error_rempty_source)
            return
        }
        if (destination.isEmpty()) {
            view.showError(R.string.redirects_error_rempty_destination)
            return
        }
        if (source == destination) {
            view.showError(R.string.redirects_error_source_and_redirection_must_be_different)
            return
        }
        if (!view.hasPermission(Manifest.permission.SEND_SMS)) {
            view.askPermission(Manifest.permission.SEND_SMS)
            return
        }
        smsVerifyCatcher = SmsVerifyCatcher(activity, OnSmsCatchListener { message ->
            Log.d(RedirectsActivity::class.java.simpleName, "Caught a SMS from $source: $message")
            sendSMS(destination, "SMS received from $source: $message")
        })
        view.redirectSetConfirmation(source, destination)
        onStart()
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.d(RedirectsActivity::class.java.simpleName, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    override fun onStart() {
        if (::smsVerifyCatcher.isInitialized)
            smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        if (::smsVerifyCatcher.isInitialized)
            smsVerifyCatcher.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (::smsVerifyCatcher.isInitialized)
            smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}