package com.pierreduchemin.smsforward.redirects

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import com.pierreduchemin.smsforward.R

const val EXTRAKEY_SOURCE: String = "EXTRAKEY_SOURCE"
const val EXTRAKEY_DESTINATION: String = "EXTRAKEY_DESTINATION"

class RedirectsService : IntentService("SMSForwardService") {

    private val tag: String = RedirectsActivity::class.java.simpleName

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun onHandleIntent(p0: Intent?) {
        smsVerifyCatcher = SmsVerifyCatcher(applicationContext, object : OnSmsReceivedListener {
            override fun onSmsReceived(source: String, message: String) {
                Log.i(tag, "Caught a SMS from $source: $message")
//                sendSMS(destination, getString(R.string.redirects_info_sms_received_from, source, message))
            }
        })
        smsVerifyCatcher.onStart()

//        val pendingIntent: PendingIntent =
//            Intent(this, ExampleActivity::class.java).let { notificationIntent ->
//                PendingIntent.getActivity(this, 0, notificationIntent, 0)
//            }
//
//        val notification: Notification = Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
//            .setContentTitle(getText(R.string.notification_title))
//            .setContentText(getText(R.string.notification_message))
//            .setSmallIcon(R.drawable.icon)
//            .setContentIntent(pendingIntent)
//            .setTicker(getText(R.string.ticker_text))
//            .build()
//
//        startForeground(ONGOING_NOTIFICATION_ID, notification)

    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.i(tag, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    fun setRedirect() {

    }
}
