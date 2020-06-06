package com.pierreduchemin.smsforward.ui.addredirect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { SmsReceiver::class.java.simpleName }
    }

    private var callback: OnSmsReceivedListener? = null
    private var phoneNumberFilter: List<ForwardModel>? = null

    fun setCallback(callback: OnSmsReceivedListener) {
        this.callback = callback
    }

    fun setPhoneNumberFilter(phoneNumberFilter: List<ForwardModel>) {
        this.phoneNumberFilter = phoneNumberFilter
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")
        val smsReceivedListener = callback
        if (smsReceivedListener == null) {
            Log.d(TAG, "No callback defined")
            return
        }
        val bundle = intent.extras ?: return
        try {
            val pdusObj = bundle.get("pdus") as Array<*>
            for (o in pdusObj) {
                if (o == null) {
                    continue
                }
                val currentMessage = getIncomingMessage(o, bundle)
                val phoneNumber = PhoneNumberUtils.toUnifiedNumber(
                    context,
                    currentMessage.displayOriginatingAddress
                )

                phoneNumberFilter?.filter {
                    it.from == phoneNumber
                }?.map {
                    val message = currentMessage.displayMessageBody
                    smsReceivedListener.onSmsReceived(it, message)
                }
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
}
