package com.pierreduchemin.smsforward.redirects

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

internal class SmsReceiver : BroadcastReceiver() {

    private var callback: OnSmsReceivedListener? = null
    private var phoneNumberFilter: String? = null
    private var filter: String? = null

    fun setCallback(callback: OnSmsReceivedListener) {
        this.callback = callback
    }

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        try {
            if (bundle == null) {
                throw Error("Null bundle")
            }

            val pdusObj = bundle.get("pdus") as Array<*>
            for (o in pdusObj) {
                if (o == null) {
                    continue
                }
                val currentMessage = getIncomingMessage(o, bundle)
                val phoneNumber = currentMessage.displayOriginatingAddress

                if (phoneNumberFilter != null && phoneNumber != phoneNumberFilter) {
                    return
                }
                val message = currentMessage.displayMessageBody
                val currentFilter = filter
                if (currentFilter != null && !message.matches(currentFilter.toRegex())) {
                    return
                }

                if (callback != null) {
                    callback!!.onSmsReceived(message)
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")
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

    fun setFilter(regularExpression: String?) {
        this.filter = regularExpression
    }
}
