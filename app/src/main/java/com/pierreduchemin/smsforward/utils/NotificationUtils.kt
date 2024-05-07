package com.pierreduchemin.smsforward.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.presentation.MainActivity

object NotificationUtils {

    private const val NOTIFICATION_ID = 1
    private const val NOTIFICATION_REQUEST_CODE: Int = 2

    fun notify(context: Context) {
        val channelId = getNotificationChannel(context)

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = getNotification(context, channelId, pendingIntent)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    fun cancel(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    private fun getNotificationChannel(context: Context): String {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            "REDIRECT_CHANNEL_ID"
        }
        return channelId
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context): String {
        val channelId = "REDIRECT_CHANNEL_ID"
        val notificationChannel =
            NotificationChannel(channelId, "SMS redirection", NotificationManager.IMPORTANCE_LOW)
        notificationChannel.lightColor = ContextCompat.getColor(context, R.color.colorPrimary)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        )
        service?.createNotificationChannel(notificationChannel)
        return channelId
    }


    private fun getNotification(
        context: Context,
        channelId: String,
        startAppPendingIntent: PendingIntent?
    ) = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_sms_forward)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(context.getString(R.string.notification_info_sms_now_redirected))
        .setContentIntent(startAppPendingIntent)
        .setOngoing(true)
        .setSilent(true)
        .setWhen(System.currentTimeMillis())
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
}