package com.pierreduchemin.smsforward.ui.redirectlist

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
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.ui.addredirect.OnSmsReceivedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject

class RedirectService : Service() {

    @Inject
    lateinit var globalModelRepository: GlobalModelRepository

    @Inject
    lateinit var forwardModelRepository: ForwardModelRepository

    private var smsReceiver: SmsReceiver = SmsReceiver()

    companion object {
        private val TAG by lazy { RedirectService::class.java.simpleName }
        private var intent: Intent? = null

        private const val ACTION_START_REDIRECT =
            "com.pierreduchemin.smsforward.buisiness.redirects.action.START_REDIRECT"
        private const val ACTION_STOP_REDIRECT =
            "com.pierreduchemin.smsforward.buisiness.redirects.action.STOP_REDIRECT"

        private const val REDIRECT_NOTIFICATION_ID: Int = 1

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
        (application as App).component.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_REDIRECT -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val globalModel = globalModelRepository.getGlobalModel()
                    if (globalModel == null || !globalModel.activated) {
                        Log.i(TAG, "Redirection not activated")
                        return@launch
                    }

                    val forwardModels = forwardModelRepository.getForwardModels()
                    if (forwardModels.isEmpty()) {
                        Log.i(TAG, "Nothing to redirect")
                        return@launch
                    }
                    handleActionRedirect(forwardModels)

                    val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        createNotificationChannel()
                    } else {
                        // If earlier version channel ID is not used
                        // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                        "REDIRECT_CHANNEL_ID"
                    }

                    val startAppIntent =
                        Intent(this@RedirectService, RedirectListActivity::class.java)
                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    val startAppPendingIntent =
                        PendingIntent.getActivity(this@RedirectService, 0, startAppIntent, 0)

                    val notification: Notification =
                        NotificationCompat.Builder(this@RedirectService, channelId)
                            .setSmallIcon(R.drawable.ic_sms_forward)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.notification_info_sms_now_redirected))
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
    private fun handleActionRedirect(dbForwardModels: List<ForwardModel>) {
        smsReceiver.setCallback(object : OnSmsReceivedListener {
            override fun onSmsReceived(phoneNumberFrom: String, message: String) {
                Log.d(TAG, "onSmsReceived")
                dbForwardModels.filter { dbForwardModel ->
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
                    sendSMS(
                        it.to, getString(
                            R.string.notification_info_sms_received_from,
                            it.vfrom,
                            message
                        )
                    )
                }
            }
        })
        smsReceiver.setPhoneNumberFilter(dbForwardModels)

        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)

        registerReceiver(smsReceiver, filter)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.d(TAG, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        val messageDivided = smsManager.divideMessage(message)

        if (messageDivided.size == 1) {
            Log.d(TAG, "Sending as single message")
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } else {
            Log.d(TAG, "Sending as multipart message")
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageDivided, null, null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "REDIRECT_CHANNEL_ID"
        val notificationChannel =
            NotificationChannel(channelId, "SMS redirection", NotificationManager.IMPORTANCE_NONE)
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
        try {
            unregisterReceiver(smsReceiver)
        } catch (e: IllegalArgumentException) {
            Log.i(TAG, "Receiver was not registered")
        }
    }
}
