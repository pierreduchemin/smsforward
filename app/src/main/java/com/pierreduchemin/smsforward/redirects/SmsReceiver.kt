package com.pierreduchemin.smsforward.redirects

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { SmsReceiver::class.java.simpleName }
    }

    private var callback: OnSmsReceivedListener? = null
    private var phoneNumberFilter: String? = null
    private var filter: String? = null

    fun setCallback(callback: OnSmsReceivedListener) {
        this.callback = callback
    }

    override fun onReceive(context: Context, intent: Intent) {
        val smsReceivedListener = callback ?: return
        val bundle = intent.extras ?: return
        try {
            val pdusObj = bundle.get("pdus") as Array<*>
            for (o in pdusObj) {
                if (o == null) {
                    continue
                }
                val currentMessage = getIncomingMessage(o, bundle)
                val phoneNumber = currentMessage.displayOriginatingAddress

                if (phoneNumberFilter != null && phoneNumber != phoneNumberFilter) {
                    Log.d(TAG, "Not the target: $phoneNumberFilter, was: $phoneNumber")
                    return
                }
                val message = currentMessage.displayMessageBody
                val currentFilter = filter
                if (currentFilter != null && !message.matches(currentFilter.toRegex())) {
                    return
                }

                smsReceivedListener.onSmsReceived(phoneNumber, message)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in smsReceiver $e")
        }
    }

    private fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage {
        val currentSMS: SmsMessage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString("format")
            currentSMS = SmsMessage.createFromPdu(aObject as ByteArray, format)
        } else {
            @Suppress("DEPRECATION")
            currentSMS = SmsMessage.createFromPdu(aObject as ByteArray)
        }
        return currentSMS
    }

    fun setPhoneNumberFilter(phoneNumberFilter: String?) {
        this.phoneNumberFilter = phoneNumberFilter
    }
}
