package com.pierreduchemin.smsforward.ui.redirectlist

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils
import com.pierreduchemin.smsforward.utils.RedirectionManager
import com.pierreduchemin.smsforward.utils.SdkUtils
import dagger.android.AndroidInjection
import javax.inject.Inject

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { SmsReceiver::class.java.simpleName }
    }

    @Inject
    lateinit var redirectionManager: RedirectionManager

    fun registerSmsReceiver(context: Context) {
        val filter = IntentFilter().apply {
            addAction("android.provider.Telephony.SMS_RECEIVED")
            addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        }

        ContextCompat.registerReceiver(
            context,
            this,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        Log.d(TAG, "Registered")
    }

    fun unregisterSmsReceiver(context: Context) {
        try {
            context.unregisterReceiver(this)
            Log.d(TAG, "Unregistered")
        } catch(e: IllegalArgumentException) {

        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        Log.d(TAG, "onReceive")
        val bundle = intent.extras ?: return
        try {
            val (phoneNumberFrom, messageContent) = readSms(bundle, context)
            redirectionManager.onSmsReceived(context, phoneNumberFrom, messageContent)
        } catch (e: Exception) {
            Log.e(TAG, "Exception in smsReceiver $e")
            Toast.makeText(context, "Error: $e", Toast.LENGTH_LONG).show()
        }
    }

    private fun readSms(
        bundle: Bundle,
        context: Context
    ): Pair<String, String> {
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
        return Pair(phoneNumberFrom, messageContent)
    }
}
