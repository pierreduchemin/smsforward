package com.pierreduchemin.smsforward.redirects

import android.Manifest
import android.app.Activity
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import com.pierreduchemin.smsforward.R

class RedirectsPresenter(
    private val activity: Activity,
//    val redirectsRepository: RedirectsRepository,
    private val view: RedirectsContract.View
) : RedirectsContract.Presenter {

    companion object {
        private val TAG by lazy { RedirectsPresenter::class.java.simpleName }
    }

    private var smsReceiver: SmsReceiver = SmsReceiver()

    init {
        view.presenter = this
    }

    override fun setRedirect(source: String, destination: String) {
        val machineSource = toMachineNumber(source)
        val machineDestination = toMachineNumber(destination)


        if (machineSource.isEmpty()) {
            view.showError(R.string.redirects_error_empty_source)
            return
        }
        if (machineDestination.isEmpty()) {
            view.showError(R.string.redirects_error_empty_destination)
            return
        }
        if (machineSource == machineDestination) {
            view.showError(R.string.redirects_error_source_and_redirection_must_be_different)
            return
        }
        if (!isValidNumber(machineSource)) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }
        if (!isValidNumber(machineDestination)) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }
        if (!view.hasPermission(Manifest.permission.SEND_SMS)) {
            view.askPermission(Manifest.permission.SEND_SMS)
            return
        }

        smsReceiver.setCallback(object : OnSmsReceivedListener {
            override fun onSmsReceived(source: String, message: String) {
                Log.i(TAG, "Caught a SMS from $source: $message")

                // db request to get destination
                sendSMS(destination, activity.getString(R.string.redirects_info_sms_received_from, source, message))
            }
        })
        smsReceiver.setPhoneNumberFilter(machineSource)

        view.redirectSetConfirmation(machineSource, machineDestination)
    }

    private fun toMachineNumber(phoneNumber: String): String {
        val regex = "\\D".toRegex()
        return regex.replace(phoneNumber, "")
    }

    private fun isValidNumber(phoneNumber: String): Boolean {
        // TODO use libphonenumber
        return phoneNumber.length in 4..16
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.i(TAG, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    override fun onStartListening() {
        activity.registerReceiver(
            smsReceiver,
            IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        )
    }

    override fun onStopListening() {
        activity.unregisterReceiver(smsReceiver)
    }
}