package com.pierreduchemin.smsforward.redirects

import android.app.Activity
import android.telephony.SmsManager
import android.util.Log
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher

class RedirectsPresenter(private val activity: Activity): RedirectsContract.Presenter {

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun start() {

    }

    override fun setRedirect(source: String, destination: String) {
        smsVerifyCatcher = SmsVerifyCatcher(activity, OnSmsCatchListener { message ->
            Log.d(RedirectsActivity::class.java.simpleName, "SMS received from $source : $message")
            sendSMS(destination, "SMS received from $source : $message")
        })
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val sms = SmsManager.getDefault()
        // TODO check valid sms manager
        sms.sendTextMessage(phoneNumber, null, message, null, null)
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
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}