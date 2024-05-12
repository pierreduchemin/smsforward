package com.pierreduchemin.smsforward.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat.getSystemService

object SdkUtils {

    @Suppress("DEPRECATION")
    fun getSmsManager(context: Context): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(context, SmsManager::class.java) ?: SmsManager.getDefault()
        } else {
            SmsManager.getDefault()
        }
    }

    @Suppress("DEPRECATION")
    fun vibrate(context: Context) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val systemService = context.getSystemService(Context.VIBRATOR_SERVICE) ?: return
                (systemService as Vibrator).vibrate(
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            }

            else -> {
                val systemService = context.getSystemService(Context.VIBRATOR_SERVICE) ?: return
                (systemService as Vibrator).vibrate(50)
            }
        }
    }

    fun isDarkTheme(resources: Resources): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}