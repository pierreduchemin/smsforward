package com.pierreduchemin.smsforward.redirects

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import com.pierreduchemin.smsforward.R

class RedirectsService : Service() {

    private val tag: String = RedirectsActivity::class.java.simpleName

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // in service
        smsVerifyCatcher = SmsVerifyCatcher(applicationContext, object : OnSmsReceivedListener {
            override fun onSmsReceived(message: String) {
//                Log.i(tag, "Caught a SMS from $source: $message")
//                sendSMS(destination, getString(R.string.redirects_info_sms_received_from, source, message))
            }
        })
        smsVerifyCatcher.onStart()
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.i(tag, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    fun setRedirect() {

    }
}
