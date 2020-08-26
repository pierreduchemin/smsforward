package com.pierreduchemin.smsforward.ui.addredirect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pierreduchemin.smsforward.R
import kotlinx.android.synthetic.main.redirects_fragment.*

class AddRedirectFragment : Fragment(), RedirectsContract.View {

    companion object {
        fun newInstance() = AddRedirectFragment()

        const val PERMISSIONS_REQUEST_SEND_SMS = 1654
        const val CONTACT_PICKER_SOURCE_REQUEST_CODE = 1456
        const val CONTACT_PICKER_DESTINATION_REQUEST_CODE = 1896
    }

    enum class ButtonState {
        DISABLED,
        ENABLED
    }

    private lateinit var viewModel: AddRedirectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AddRedirectViewModel::class.java)
        viewModel.buttonState.observe(requireActivity(), {
            setButtonState(it)
        })
        viewModel.errorMessageRes.observe(requireActivity(), {
            showError(it)
        })
        viewModel.sourceText.observe(requireActivity(), {
            setSource(it)
        })
        viewModel.destinationText.observe(requireActivity(), {
            setDestination(it)
        })
        viewModel.isComplete.observe(requireActivity(), {
            if (it) {
                requireActivity().finish()
            }
        })

        return inflater.inflate(R.layout.redirects_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSource.setOnClickListener { pickNumber(CONTACT_PICKER_SOURCE_REQUEST_CODE) }
        etDestination.setOnClickListener { pickNumber(CONTACT_PICKER_DESTINATION_REQUEST_CODE) }
        btnAdd.setOnClickListener {
            if (!hasPermission(Manifest.permission.SEND_SMS)) {
                askPermission(Manifest.permission.SEND_SMS)
                return@setOnClickListener
            }
            if (!hasPermission(Manifest.permission.RECEIVE_SMS)) {
                askPermission(Manifest.permission.RECEIVE_SMS)
                return@setOnClickListener
            }
            viewModel.onButtonClicked(
                etSource.text.trim().toString(),
                etDestination.text.trim().toString()
            )
        }
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
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val phoneIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                phoneNo = cursor.getString(phoneIndex)
            }
            cursor.close()
        }

        if (requestCode == CONTACT_PICKER_SOURCE_REQUEST_CODE) {
            viewModel.onSourceRetrieved(phoneNo)
        } else if (requestCode == CONTACT_PICKER_DESTINATION_REQUEST_CODE) {
            viewModel.onDestinationRetrieved(phoneNo)
        }
    }

    override fun setButtonState(buttonState: ButtonState) {
        when (buttonState) {
            ButtonState.DISABLED -> {
                etSource.isEnabled = true
                etDestination.isEnabled = true
                btnAdd.isEnabled = false
                btnAdd.text = getString(R.string.addredirect_info_add)
            }
            ButtonState.ENABLED -> {
                etSource.isEnabled = true
                etDestination.isEnabled = true
                btnAdd.isEnabled = true
                btnAdd.text = getString(R.string.addredirect_info_add)
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
                R.string.addredirect_info_sms_redirect_confirmation,
                source,
                destination
            ),
            Toast.LENGTH_SHORT
        ).show()
    }
}
