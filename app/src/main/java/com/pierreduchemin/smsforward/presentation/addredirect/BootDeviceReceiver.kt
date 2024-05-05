package com.pierreduchemin.smsforward.presentation.addredirect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pierreduchemin.smsforward.presentation.redirectlist.RedirectService

class BootDeviceReceiver : BroadcastReceiver() {

    companion object {
        private val TAG by lazy { BootDeviceReceiver::class.java.simpleName }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "BootDeviceReceiver onReceive, action is $action")
        if (Intent.ACTION_BOOT_COMPLETED == action) {
            RedirectService.startActionRedirect(context)
        }
    }
}