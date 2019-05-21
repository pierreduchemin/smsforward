package com.pierreduchemin.smsforward

import android.app.Activity
import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import kotlinx.android.synthetic.main.activity_main.*

class ListenSMSWorker(
    private val activity: Activity,
    private val source: String,
    private val destination: String,
    workerParams: WorkerParameters
) : Worker(activity, workerParams) {

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun doWork(): Result {

        // TODO validate destination
        smsVerifyCatcher = SmsVerifyCatcher(activity, OnSmsCatchListener { message ->
            Log.d(MainActivity::class.java.simpleName, "SMS received from $source : $message")
            sendSMS(destination, "SMS received from $source : $message")
        })

        // TODO validate source
//        smsVerifyCatcher.setPhoneNumberFilter(etSource.toString())

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val sms = SmsManager.getDefault()
        // TODO check valid sms manager
        sms.sendTextMessage(phoneNumber, null, message, null, null)
    }
}
