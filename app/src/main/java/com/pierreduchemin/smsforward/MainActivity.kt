package com.pierreduchemin.smsforward

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.workDataOf
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher


class MainActivity : AppCompatActivity() {

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val a = ListenSMSWorker(this, "test", "test", workDataOf())
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
}
