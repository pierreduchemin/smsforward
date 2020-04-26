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
package com.pierreduchemin.smsforward.ui.addredirect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pierreduchemin.smsforward.R

const val PERMISSIONS_REQUEST_SEND_SMS = 1654
const val CONTACT_PICKER_SOURCE_REQUEST_CODE = 1456
const val CONTACT_PICKER_DESTINATION_REQUEST_CODE = 1896

class RedirectsFragment : Fragment(), RedirectsContract.View {

    companion object {
        private val TAG by lazy { RedirectsFragment::class.java.simpleName }
        fun newInstance() = RedirectsFragment()
    }

    enum class ButtonState {
        DISABLED,
        ENABLED,
        STOP
    }

    private lateinit var viewModel: AddRedirectViewModel

    private lateinit var root: View
    private lateinit var btnEnable: Button
    private lateinit var etSource: EditText
    private lateinit var etDestination: EditText

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
            if (!hasPermission(Manifest.permission.SEND_SMS)) {
                askPermission(Manifest.permission.SEND_SMS)
                return@setOnClickListener
            }
            viewModel.onButtonClicked(
                etSource.text.trim().toString(),
                etDestination.text.trim().toString()
            )
        }

        viewModel = ViewModelProvider(this).get(AddRedirectViewModel::class.java)
        viewModel.buttonState.observe(requireActivity(), Observer {
            setButtonState(it)
        })
        viewModel.errorMessageRes.observe(requireActivity(), Observer {
            showError(it)
        })
        viewModel.sourceText.observe(requireActivity(), Observer {
            setSource(it)
        })
        viewModel.destinationText.observe(requireActivity(), Observer {
            setDestination(it)
        })

        viewModel.onViewCreated()

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECEIVE_SMS),
            REQUEST_CODE_SMS_PERMISSION
        )

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

        cursor.close()

        if (requestCode == CONTACT_PICKER_SOURCE_REQUEST_CODE) {
            viewModel.onSourceRetrieved(phoneNo)
        } else if (requestCode == CONTACT_PICKER_DESTINATION_REQUEST_CODE) {
            viewModel.onDestinationRetrieved(phoneNo)
        }

        viewModel.onNumberPicked()
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

    override fun showRedirectMessage(source: String, destination: String) {
        Toast.makeText(
            requireActivity().applicationContext,
            requireContext().getString(
                R.string.redirects_info_sms_redirect_confirmation,
                source,
                destination
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            Log.i(TAG, "REQUEST_CODE_SMS_PERMISSION ok")
            viewModel.onViewCreated()
        }
    }
}
