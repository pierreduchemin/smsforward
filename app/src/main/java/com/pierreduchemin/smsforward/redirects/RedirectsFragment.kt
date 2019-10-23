/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pierreduchemin.smsforward.redirects

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pierreduchemin.smsforward.R

const val PERMISSIONS_REQUEST_SEND_SMS = 1654
const val CONTACT_PICKER_SOURCE_REQUEST_CODE = 1456
const val CONTACT_PICKER_DESTINATION_REQUEST_CODE = 1896

class RedirectsFragment : Fragment(), RedirectsContract.View {

    enum class ButtonState {
        DISABLED,
        ENABLED,
        STOP
    }

    override lateinit var presenter: RedirectsContract.Presenter

    private lateinit var root: View
    private lateinit var btnEnable: Button
    private lateinit var etSource: EditText
    private lateinit var etDestination: EditText

    companion object {
        fun newInstance() = RedirectsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.redirects_fragment, container, false)

        btnEnable = root.findViewById(R.id.btnEnable)
        etSource = root.findViewById(R.id.etSource)
        etSource.setOnClickListener { pickNumber(CONTACT_PICKER_SOURCE_REQUEST_CODE) }
        etDestination = root.findViewById(R.id.etDestination)
        etDestination.setOnClickListener { pickNumber(CONTACT_PICKER_DESTINATION_REQUEST_CODE) }
        btnEnable.setOnClickListener {
            presenter.onButtonClicked(
                etSource.text.trim().toString(),
                etDestination.text.trim().toString()
            )
        }

        presenter.onViewCreated()

        return root
    }

    override fun hasPermission(permissionString: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permissionString
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun askPermission(permissionString: String) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(permissionString),
            PERMISSIONS_REQUEST_SEND_SMS
        )
    }

    override fun showError(message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun pickNumber(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null) {
            return
        }
        var phoneNo: String? = null
        val uri = data.data!!
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)!!

        if (cursor.moveToFirst()) {
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            phoneNo = cursor.getString(phoneIndex)
        }

        if (requestCode == CONTACT_PICKER_SOURCE_REQUEST_CODE) {
            presenter.onSourceSet(phoneNo)
        } else if (requestCode == CONTACT_PICKER_DESTINATION_REQUEST_CODE) {
            presenter.onDestinationSet(phoneNo)
        }

        cursor.close()

        presenter.onNumberPicked()
    }

    override fun setButtonState(buttonState: ButtonState) {
        when (buttonState) {
            ButtonState.DISABLED -> {
                etSource.isEnabled = true
                etDestination.isEnabled = true
                btnEnable.isEnabled = false
                btnEnable.text = getString(R.string.redirects_info_enable)
            }
            ButtonState.ENABLED -> {
                etSource.isEnabled = true
                etDestination.isEnabled = true
                btnEnable.isEnabled = true
                btnEnable.text = getString(R.string.redirects_info_enable)
            }
            ButtonState.STOP -> {
                etSource.isEnabled = false
                etDestination.isEnabled = false
                btnEnable.isEnabled = true
                btnEnable.text = getString(R.string.redirects_info_stop)
            }
        }
    }

    override fun setSource(source: String) {
        etSource.setText(source, TextView.BufferType.EDITABLE)
    }

    override fun setDestination(destination: String) {
        etDestination.setText(destination, TextView.BufferType.EDITABLE)
    }

    override fun resetFields() {
        etSource.setText("", TextView.BufferType.EDITABLE)
        etDestination.setText("", TextView.BufferType.EDITABLE)
    }
}
