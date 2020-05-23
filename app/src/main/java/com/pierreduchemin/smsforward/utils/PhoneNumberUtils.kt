package com.pierreduchemin.smsforward.utils

import android.content.Context
import android.telephony.TelephonyManager
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.*

class PhoneNumberUtils {

    companion object {

        private val phoneUtil = PhoneNumberUtil.getInstance()

        private fun getProto(context: Context, phoneNumber: String): Phonenumber.PhoneNumber? {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val countryCode = telephonyManager?.simCountryIso?.toUpperCase(Locale.ROOT) ?: "FR"
            return try {
                phoneUtil.parse(phoneNumber, countryCode)
            } catch (e: RuntimeException) {
                null
            } catch (npe: NumberParseException) {
                null
            }
        }

        fun toUnifiedNumber(context: Context, phoneNumber: String): String {
            val proto = getProto(context, phoneNumber) ?: return phoneNumber
            return phoneUtil.format(proto, PhoneNumberUtil.PhoneNumberFormat.E164)
        }

        fun toVisualNumber(context: Context, phoneNumber: String): String {
            val proto = getProto(context, phoneNumber) ?: return phoneNumber
            return phoneUtil.format(proto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        }
    }
}