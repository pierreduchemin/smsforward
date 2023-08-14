package com.pierreduchemin.smsforward.data.source.aosp

import android.content.Context
import android.provider.ContactsContract
import com.pierreduchemin.smsforward.data.ContactModel

object AospSource {

    fun getContact(context: Context): ContactModel? {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        context.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.apply {
            if (count > 0) {
                moveToFirst()

                val columnIndexKey =
                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)
                val columnIndexName =
                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val columnIndexNumber =
                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val contactModel = ContactModel(
                    getString(columnIndexKey),
                    getString(columnIndexName),
                    getString(columnIndexNumber)
                )
                close()
                return contactModel
            }
        }
        return null
    }
}