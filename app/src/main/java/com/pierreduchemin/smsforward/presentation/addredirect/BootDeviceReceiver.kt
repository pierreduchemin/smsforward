package com.pierreduchemin.smsforward.presentation.addredirect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pierreduchemin.smsforward.presentation.redirectlist.SmsReceiver
import com.pierreduchemin.smsforward.utils.NotificationUtils
import com.pierreduchemin.smsforward.utils.RedirectionManager
import javax.inject.Inject

class BootDeviceReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { BootDeviceReceiver::class.java.simpleName }
    }

    @Inject
    lateinit var redirectionManager: RedirectionManager

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "onReceive, action is $action")
        if (action != Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "onReceive, action ($action) ignored")
            return
        }

        if (!redirectionManager.isRedirectionActivated()) {
            Log.i(TAG, "Message received but redirection is not activated")
            return
        }

        val smsReceiver = SmsReceiver()
        smsReceiver.registerSmsReceiver(context)
        NotificationUtils.notify(context)
    }
}