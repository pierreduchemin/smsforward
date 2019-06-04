package com.pierreduchemin.smsforward.redirects

import android.Manifest
import android.app.Activity
import android.telephony.SmsManager
import android.util.Log
import com.pierreduchemin.smsforward.R

class RedirectsPresenter(
    private val activity: Activity,
//    val redirectsRepository: RedirectsRepository,
    private val view: RedirectsContract.View
) : RedirectsContract.Presenter {

    private val tag: String = RedirectsActivity::class.java.simpleName

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    init {
        view.presenter = this
    }

    override fun start() {

    }

    override fun setRedirect(source: String, destination: String) {
        if (source.isEmpty()) {
            view.showError(R.string.redirects_error_empty_source)
            return
        }
        if (destination.isEmpty()) {
            view.showError(R.string.redirects_error_empty_destination)
            return
        }
        if (source == destination) {
            view.showError(R.string.redirects_error_source_and_redirection_must_be_different)
            return
        }
        if (!isValidNumber(source)) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }
        if (!isValidNumber(destination)) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }
        if (!view.hasPermission(Manifest.permission.SEND_SMS)) {
            view.askPermission(Manifest.permission.SEND_SMS)
            return
        }

        // in service
        smsVerifyCatcher = SmsVerifyCatcher(activity, object : OnSmsReceivedListener {
            override fun onSmsReceived(message: String) {
                Log.i(tag, "Caught a SMS from $source: $message")
                sendSMS(destination, activity.getString(R.string.redirects_info_sms_received_from, source, message))
            }
        })
        smsVerifyCatcher.onStart()

        view.redirectSetConfirmation(source, destination)
    }

    private fun isValidNumber(phoneNumber: String): Boolean {
        return phoneNumber.length >= 4 || phoneNumber.length <= 16
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.i(tag, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    override fun onStart() {
    }

    override fun onStop() {
        if (::smsVerifyCatcher.isInitialized)
            smsVerifyCatcher.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (::smsVerifyCatcher.isInitialized)
//            smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}