package com.pierreduchemin.smsforward.utils

import android.content.Context
import android.util.Log
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject

class RedirectionManager @Inject constructor(
    private val globalModelRepository: GlobalModelRepository,
    private val forwardModelRepository: ForwardModelRepository
) {

    companion object {
        private val TAG by lazy { RedirectionManager::class.java.simpleName }
    }

    fun onSmsReceived(context: Context, phoneNumberFrom: String, message: String) {
        if (!isRedirectionActivated()) {
            Log.i(TAG, "Redirection not activated")
            return
        }

        val forwardModels = forwardModelRepository.getForwardModels()
        if (forwardModels.isEmpty()) {
            Log.i(TAG, "Nothing to redirect")
            return
        }

        Log.d(TAG, "SMS received from $phoneNumberFrom")
        forwardModels.filter { dbForwardModel ->
            if (dbForwardModel.isRegex) {
                try {
                    Pattern.compile(dbForwardModel.from).matcher(phoneNumberFrom).matches()
                } catch (e: PatternSyntaxException) {
                    Log.e(
                        TAG,
                        "Invalid pattern. This should not happen as regex are supposed to be already validated."
                    )
                    false
                }
            } else {
                dbForwardModel.from == phoneNumberFrom
            }
        }.map {
            Log.d(TAG, "Caught a SMS from $phoneNumberFrom that matches ${it.from}")
            var source = phoneNumberFrom
            if (it.vfromName.isNotBlank()) {
                source = it.vfromName + " | " + phoneNumberFrom
            }

            sendSMS(
                context, it.to, context.getString(
                    R.string.notification_info_sms_received_from,
                    source,
                    message
                )
            )
        }
    }

    fun isRedirectionActivated(): Boolean {
        val globalModel = globalModelRepository.getGlobalModel()
        if (globalModel == null) {
            Log.d(TAG, "globalModel is null")
            return true
        }
        if (!globalModel.activated) {
            Log.i(TAG, "Redirection not activated")
            return true
        }
        return false
    }

    private fun sendSMS(context: Context, phoneNumber: String, message: String) {
        Log.d(TAG, "Sending SMS to $phoneNumber: $message")
        val smsManager = SdkUtils.getSmsManager(context)
        val messageDivided = smsManager.divideMessage(message)

        if (messageDivided.size == 1) {
            Log.d(TAG, "Sending as single message")
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } else {
            Log.d(TAG, "Sending as multipart message")
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageDivided, null, null)
        }
    }
}