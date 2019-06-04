package com.pierreduchemin.smsforward.redirects

import android.content.Context
import android.content.IntentFilter

class SmsVerifyCatcher(
    private val context: Context,
    private val onSmsReceivedListener: OnSmsReceivedListener
) {
    private var smsReceiver: SmsReceiver
    private var phoneNumber: String? = null
    private var filter: String? = null

    init {
        smsReceiver = SmsReceiver()
        smsReceiver.setCallback(this.onSmsReceivedListener)
    }

    private fun registerReceiver() {
        if (phoneNumber == null && filter == null) {
            return
        }
        smsReceiver = SmsReceiver()
        smsReceiver.setCallback(onSmsReceivedListener)
        smsReceiver.setPhoneNumberFilter(phoneNumber)
        smsReceiver.setFilter(filter)
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED")
        context.registerReceiver(smsReceiver, intentFilter)
    }

    fun setPhoneNumberFilter(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun setFilter(regexp: String) {
        this.filter = regexp
    }

    fun onStart() {
        registerReceiver()
    }

    fun onStop() {
        try {
            context.unregisterReceiver(smsReceiver)
        } catch (ignore: IllegalArgumentException) {
            //receiver not registered
        }
    }
}