package com.pierreduchemin.smsforward.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.pierreduchemin.smsforward.R

object NotificationUtils {

    fun notify(context: Context) {
        val channelId = getNotificationChannel(context)

        val notification = getNotification(context, channelId, null)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
//        NotificationManagerCompat.from(context).notify(notification)

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
//        val notificationChannel =
//            NotificationChannel(channelId, "SMS redirection", NotificationManager.IMPORTANCE_NONE)
//        notificationChannel.lightColor = ContextCompat.getColor(context, R.color.colorPrimary)
//        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//        val service = ContextCompat.getSystemService(
//            context,
//            Context.NOTIFICATION_SERVICE
//        ) as NotificationManager
//        service.createNotificationChannel(notificationChannel)
        return channelId
    }


    fun getNotification(
        context: Context,
        channelId: String,
        startAppPendingIntent: PendingIntent?
    ) = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_sms_forward)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(context.getString(R.string.notification_info_sms_now_redirected))
        .setContentIntent(startAppPendingIntent)
        .setWhen(System.currentTimeMillis())
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .build()
}