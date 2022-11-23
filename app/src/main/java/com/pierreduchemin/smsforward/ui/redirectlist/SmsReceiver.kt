package com.pierreduchemin.smsforward.ui.redirectlist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
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
            val pdusObj = bundle.getStringArray("pdus") as Array<String>
            var phoneNumberFrom = ""
            var messageContent = ""
            for (o in pdusObj) {
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

    fun deleteSMS(context: Context, message: String, number: String) {
        try {
            val uriSms: Uri = Uri.parse("content://sms/inbox")
            val c: Cursor? = context.contentResolver.query(
                uriSms, arrayOf(
                    "_id", "thread_id", "address",
                    "person", "date", "body"
                ), null, null, null
            )
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        val id: Long = c.getLong(0)
                        val address: String = c.getString(2)
                        val body: String = c.getString(5)
                        if (message == body && address == number) {
                            context.contentResolver.delete(
                                Uri.parse("content://sms/$id"), null, null
                            )
                        }
                    } while (c.moveToNext())
                }
                c.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not delete SMS from inbox: " + e.message)
        }
    }
}
