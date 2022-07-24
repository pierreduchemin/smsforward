package com.pierreduchemin.smsforward.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SmsMessage
import androidx.core.content.ContextCompat.getSystemService

class SdkUtils {

    companion object {

        @Suppress("DEPRECATION")
        fun getSmsManager(context: Context): SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(context, SmsManager::class.java) ?: SmsManager.getDefault()
        } else {
            SmsManager.getDefault()
        }

        @Suppress("DEPRECATION")
        fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage {
            val currentSMS: SmsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val format = bundle.getString("format")
                SmsMessage.createFromPdu(aObject as ByteArray, format)
            } else {
                SmsMessage.createFromPdu(aObject as ByteArray)
            }
            return currentSMS
        }
    }
}