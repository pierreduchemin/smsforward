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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pierreduchemin.smsforward.R
import kotlinx.android.synthetic.main.add_redirects_fragment.*

class AddRedirectFragment : Fragment(), AddRedirectContract.View {

    companion object {
        fun newInstance() = AddRedirectFragment()

        const val PERMISSIONS_REQUEST_CODE = 1655
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

        return inflater.inflate(R.layout.add_redirects_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSource.setOnClickListener { pickNumber(CONTACT_PICKER_SOURCE_REQUEST_CODE) }
        etDestination.setOnClickListener { pickNumber(CONTACT_PICKER_DESTINATION_REQUEST_CODE) }
        btnAdd.setOnClickListener {
            val missingPermissions: ArrayList<String> = arrayListOf()

            if (!hasPermission(Manifest.permission.SEND_SMS)) {
                missingPermissions.add(Manifest.permission.SEND_SMS)
            }
            if (!hasPermission(Manifest.permission.RECEIVE_SMS)) {
                missingPermissions.add(Manifest.permission.RECEIVE_SMS)
            }
            if (missingPermissions.isNotEmpty()) {
                askPermission(missingPermissions)
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
            requireContext(),
            permissionString
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun askPermission(permissionsString: ArrayList<String>) {
        requestPermissions(permissionsString.toTypedArray(), PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty()
                        && grantResults.all { p -> p == PackageManager.PERMISSION_GRANTED })) {
                viewModel.onButtonClicked(
                    etSource.text.trim().toString(),
                    etDestination.text.trim().toString()
                )
            } else {
                showError(R.string.addredirect_warning_permission_refused)
            }
            return
        }
    }

    override fun showError(message: Int) {
        Snackbar.make(addredirectContainer, message, Snackbar.LENGTH_LONG).show()
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
}
