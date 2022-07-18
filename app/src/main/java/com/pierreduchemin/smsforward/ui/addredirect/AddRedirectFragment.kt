package com.pierreduchemin.smsforward.ui.addredirect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.databinding.AddRedirectsFragmentBinding

class AddRedirectFragment : Fragment(), AddRedirectContract.View {

    companion object {
        fun newInstance() = AddRedirectFragment()

        const val CONTACT_PICKER_SOURCE_REQUEST_CODE = 1456
        const val CONTACT_PICKER_DESTINATION_REQUEST_CODE = 1896
    }

    enum class ButtonState {
        DISABLED,
        ENABLED
    }

    private val requiredPermissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CONTACTS
    )

    private lateinit var ui: AddRedirectsFragmentBinding
    private lateinit var viewModel: AddRedirectViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ui = AddRedirectsFragmentBinding.inflate(layoutInflater, container, false)
        askPermission(requiredPermissions)

        viewModel = ViewModelProvider(this)[AddRedirectViewModel::class.java]
        viewModel.buttonState.observe(requireActivity()) {
            setButtonState(it)
        }
        viewModel.errorMessageRes.observe(requireActivity()) {
            showError(it)
        }
        viewModel.sourceText.observe(requireActivity()) {
            setSource(it)
        }
        viewModel.destinationText.observe(requireActivity()) {
            setDestination(it)
        }
        viewModel.isComplete.observe(requireActivity()) {
            if (it) {
                requireActivity().finish()
            }
        }
        viewModel.isAdvancedModeEnabled.observe(requireActivity()) {
            if (it != null && it) {
                setAdvancedMode()
            } else {
                setNormalMode()
            }
        }

        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.etSource.setOnClickListener {
            pickNumber(CONTACT_PICKER_SOURCE_REQUEST_CODE)
        }
        ui.etDestination.setOnClickListener {
            pickNumber(CONTACT_PICKER_DESTINATION_REQUEST_CODE)
        }
        ui.btnAdd.setOnClickListener {
            viewModel.onButtonClicked(
                ui.etSource.text.trim().toString(),
                ui.etDestination.text.trim().toString()
            )
        }
        ui.btnAdvancedMode.setOnClickListener {
            viewModel.toggleMode()
        }
    }

    override fun hasPermission(permissionString: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permissionString
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun askPermission(permissionsString: Array<String>) {
        val missingPermissions = permissionsString.filter { !hasPermission(it) }
        if (missingPermissions.isNotEmpty()) {
            requireActivity().registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val permissionsOk = permissions.entries.all { it.value }
                if (!permissionsOk) {
                    showError(R.string.addredirect_warning_permission_refused)
                }
            }.launch(missingPermissions.toTypedArray())
        }
    }

    override fun showError(message: Int) {
        Snackbar.make(ui.addredirectContainer, message, Snackbar.LENGTH_LONG).show()
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
        var displayName: String? = null
        val uri = data.data!!
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val phoneIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameSourceIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_SOURCE)
                phoneNo = cursor.getString(phoneIndex)
                if (cursor.getString(nameSourceIndex) == ContactsContract.DisplayNameSources.STRUCTURED_NAME.toString()) {
                    val nameIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    displayName = cursor.getString(nameIndex)
                }
            }
            cursor.close()
        }

        if (requestCode == CONTACT_PICKER_SOURCE_REQUEST_CODE) {
            viewModel.onSourceRetrieved(phoneNo, displayName)
        } else if (requestCode == CONTACT_PICKER_DESTINATION_REQUEST_CODE) {
            viewModel.onDestinationRetrieved(phoneNo)
        }
    }

    override fun setButtonState(buttonState: ButtonState) {
        when (buttonState) {
            ButtonState.DISABLED -> {
                ui.etSource.isEnabled = true
                ui.etDestination.isEnabled = true
                ui.btnAdd.isEnabled = false
                ui.btnAdd.text = getString(R.string.addredirect_info_add)
            }
            ButtonState.ENABLED -> {
                ui.etSource.isEnabled = true
                ui.etDestination.isEnabled = true
                ui.btnAdd.isEnabled = true
                ui.btnAdd.text = getString(R.string.addredirect_info_add)
            }
        }
    }

    override fun setSource(source: String) {
        ui.etSource.setText(source, TextView.BufferType.EDITABLE)
    }

    override fun setDestination(destination: String) {
        ui.etDestination.setText(destination, TextView.BufferType.EDITABLE)
    }

    override fun resetFields() {
        ui.etSource.setText("", TextView.BufferType.EDITABLE)
        ui.etDestination.setText("", TextView.BufferType.EDITABLE)
    }

    private fun setNormalMode() {
        ui.etSource.setOnClickListener { pickNumber(CONTACT_PICKER_SOURCE_REQUEST_CODE) }
        ui.etSource.isFocusableInTouchMode = false
        ui.etSource.clearFocus()
        ui.etSource.inputType = EditorInfo.TYPE_CLASS_PHONE
        hideKeyboardFrom(ui.etSource)
        ui.btnAdvancedMode.setImageResource(R.drawable.ic_regex_grey_24dp)
    }

    private fun setAdvancedMode() {
        ui.etSource.setOnClickListener { }
        ui.etSource.isFocusableInTouchMode = true
        ui.etSource.requestFocus()
        ui.etSource.inputType = EditorInfo.TYPE_CLASS_TEXT
        ui.btnAdvancedMode.setImageResource(R.drawable.ic_regex_black_24dp)
    }

    private fun hideKeyboardFrom(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
