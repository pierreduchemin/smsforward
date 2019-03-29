package com.pierreduchemin.smsforward

import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO validate destination
        smsVerifyCatcher = SmsVerifyCatcher(this, OnSmsCatchListener { message ->
            Log.d(MainActivity::class.java.simpleName, "SMS received from ${etSource.text} : $message")
            sendSMS(etDestination.toString(), "SMS received from ${etSource.text} : $message")
        })

        // TODO validate source
//        smsVerifyCatcher.setPhoneNumberFilter(etSource.toString())
    }

    override fun onStart() {
        super.onStart()
        if (::smsVerifyCatcher.isInitialized)
            smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (::smsVerifyCatcher.isInitialized)
            smsVerifyCatcher.onStop()
    }

    /**
     * need for Android 6 real time permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val sms = SmsManager.getDefault()
        // TODO check valid sms manager
        sms.sendTextMessage(phoneNumber, null, message, null, null)
    }
}
