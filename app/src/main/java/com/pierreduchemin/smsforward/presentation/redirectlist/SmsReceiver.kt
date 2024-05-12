package com.pierreduchemin.smsforward.presentation.redirectlist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils
import com.pierreduchemin.smsforward.utils.RedirectionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { SmsReceiver::class.java.simpleName }
    }

    @Inject
    lateinit var redirectionManager: RedirectionManager

    fun registerSmsReceiver(context: Context) {
        val filter = IntentFilter().apply {
            addAction(SMS_RECEIVED_ACTION)
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
        } catch (e: IllegalArgumentException) {
// TODO: better handling
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != SMS_RECEIVED_ACTION) {
            return
        }
        val messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        Log.d(TAG, "onReceive" + messagesFromIntent.size)

        var message = ""
        messagesFromIntent.forEach {
            message += if (it.isEmail) {
                it.emailBody
            } else {
                it.messageBody
            }
        }

        val first = messagesFromIntent.first()
        if (first.isEmail) {
            redirectionManager.onSmsReceived(context, first.emailFrom, message)
        } else {
            redirectionManager.onSmsReceived(
                context,
                PhoneNumberUtils.toUnifiedNumber(context, first.displayOriginatingAddress),
                message
            )
        }
    }
}
