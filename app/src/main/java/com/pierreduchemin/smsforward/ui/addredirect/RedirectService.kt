package com.pierreduchemin.smsforward.ui.addredirect

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.di.ActivityModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject


private const val ACTION_START_REDIRECT =
    "com.pierreduchemin.smsforward.buisiness.redirects.action.START_REDIRECT"
private const val ACTION_STOP_REDIRECT =
    "com.pierreduchemin.smsforward.buisiness.redirects.action.STOP_REDIRECT"

private const val REDIRECT_NOTIFICATION_ID: Int = 1

class RedirectService : Service() {

    private val forwardModelRepository: ForwardModelRepository by inject<ForwardModelRepository>()
    private var smsReceiver: SmsReceiver = SmsReceiver()

    companion object {

        private val TAG by lazy { RedirectService::class.java.simpleName }
        private var intent: Intent? = null

        /**
         * Starts this service to perform action Redirect with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startActionRedirect(context: Context) {
            intent = Intent(context, RedirectService::class.java).apply {
                action = ACTION_START_REDIRECT
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        @JvmStatic
        fun stopActionRedirect(context: Context) {
            context.stopService(Intent(context, RedirectService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "RedirectService created")

        KTP.openRootScope()
            .openSubScope(App.APPSCOPE)
            .openSubScope(this)
            .installModules(ActivityModule(this))
            .inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_REDIRECT -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val forwardModel = forwardModelRepository.getForwardModel()
                    if (forwardModel == null) {
                        Log.e(TAG, "Invalid model in database. Not able to continue.")
                        return@launch
                    }

                    val source = forwardModel.from
                    val destination = forwardModel.to
                    handleActionRedirect(source, destination)

                    val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createNotificationChannel("REDIRECT_CHANNEL_ID", "SMS redirection")
                    } else {
                        // If earlier version channel ID is not used
                        // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                        "REDIRECT_CHANNEL_ID"
                    }

                    val startAppIntent = Intent(this@RedirectService, RedirectsActivity::class.java)
                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    val startAppPendingIntent =
                        PendingIntent.getActivity(this@RedirectService, 0, startAppIntent, 0)

                    val notification: Notification =
                        NotificationCompat.Builder(this@RedirectService, channelId)
                            .setSmallIcon(R.drawable.ic_sms_forward)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.redirects_info_sms_now_redirected))
                            .setContentIntent(startAppPendingIntent)
                            .setWhen(System.currentTimeMillis())
                            .setPriority(NotificationCompat.PRIORITY_MIN)
                            .build()

                    Log.d(TAG, "Notification started")

                    startForeground(REDIRECT_NOTIFICATION_ID, notification)
                }
            }
            ACTION_STOP_REDIRECT -> {
                unregisterReceiver(smsReceiver)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Handle action Redirect in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionRedirect(source: String, destination: String) {
        smsReceiver.setCallback(object : OnSmsReceivedListener {
            override fun onSmsReceived(source: String, message: String) {
                Log.d(TAG, "Caught a SMS from $source")
                sendSMS(
                    destination, getString(
                        R.string.redirects_info_sms_received_from,
                        source,
                        message
                    )
                )
            }
        })
        smsReceiver.setPhoneNumberFilter(source)

        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)

        registerReceiver(smsReceiver, filter)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.d(TAG, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        notificationChannel.lightColor = ContextCompat.getColor(this, R.color.colorPrimary)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(notificationChannel)
        return channelId
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw NotImplementedError()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }
}
