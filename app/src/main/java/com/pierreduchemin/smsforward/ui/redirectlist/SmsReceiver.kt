package com.pierreduchemin.smsforward.ui.redirectlist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.ui.addredirect.OnSmsReceivedListener
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils
import com.pierreduchemin.smsforward.utils.SdkUtils

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { SmsReceiver::class.java.simpleName }
    }

    private var callback: OnSmsReceivedListener? = null
    private var forwardModels: List<ForwardModel>? = null

    fun setCallback(callback: OnSmsReceivedListener) {
        this.callback = callback
    }

    fun setPhoneNumberFilter(forwardModels: List<ForwardModel>) {
        this.forwardModels = forwardModels
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
            var phoneNumberFrom = ""
            var messageContent = ""
            for (o in pdusObj) {
                if (o == null) {
                    continue
                }
                val currentMessage = SdkUtils.getIncomingMessage(o, bundle)
                if (phoneNumberFrom.isEmpty()) {
                    phoneNumberFrom = PhoneNumberUtils.toUnifiedNumber(
                        context,
                        currentMessage.displayOriginatingAddress
                    )
                }
                messageContent += currentMessage.displayMessageBody
            }
            smsReceivedListener.onSmsReceived(phoneNumberFrom, messageContent)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in smsReceiver $e")
            Toast.makeText(context, "Error: $e", Toast.LENGTH_LONG).show()
        }
    }
}
