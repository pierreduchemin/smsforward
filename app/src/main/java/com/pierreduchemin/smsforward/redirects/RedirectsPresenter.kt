package com.pierreduchemin.smsforward.redirects

import android.app.Activity
import android.telephony.SmsManager
import android.util.Log
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
        smsVerifyCatcher = SmsVerifyCatcher(activity, OnSmsCatchListener { message ->
            Log.d(RedirectsActivity::class.java.simpleName, "SMS received from $source : $message")
            sendSMS(destination, "SMS received from $source : $message")
        })
        view.redirectSetConfirmation(source, destination)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
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
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}