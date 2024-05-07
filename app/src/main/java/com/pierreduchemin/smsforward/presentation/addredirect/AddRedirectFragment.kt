package com.pierreduchemin.smsforward.presentation.addredirect

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ContactModelRepository
import com.pierreduchemin.smsforward.databinding.AddRedirectsFragmentBinding
import com.pierreduchemin.smsforward.presentation.PermissionRegisterer

class AddRedirectFragment : Fragment(), AddRedirectSubscriber {

    companion object {
        private val TAG by lazy { BootDeviceReceiver::class.java.simpleName }
    }

    sealed interface ButtonState {
        object Disabled: ButtonState
        object Enabled: ButtonState
    }

    private val requiredPermissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CONTACTS
    )

    private lateinit var ui: AddRedirectsFragmentBinding
    private val viewModel by lazy { ViewModelProvider(this)[AddRedirectViewModel::class.java] }
    private lateinit var registerForPermissions: ActivityResultLauncher<Array<String>>

    private lateinit var registerForNumberPicker: ActivityResultLauncher<Void?>
    private lateinit var registerForNumberPicker2: ActivityResultLauncher<Void?>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ui = AddRedirectsFragmentBinding.inflate(layoutInflater, container, false)

        extracted()
        extracted2()

        setupToolbar()

        val permissionRegisterer = activity as? PermissionRegisterer
        permissionRegisterer?.let {
            it.registerForPermission(this)
            askPermission(requiredPermissions)
        } ?: Log.e(TAG, "Not able to ask for permission") // TODO: manage error with error view

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
                startRedirectList()
            }
        }
        viewModel.isAdvancedModeEnabled.observe(requireActivity()) {
            if (it) {
                setAdvancedMode()
            } else {
                setNormalMode()
            }
        }

        return ui.root
    }

    private fun setupToolbar() {
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(ui.toolbar.toolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ui.toolbar.ivHelp.isVisible = false
    }

    private fun startRedirectList() {
        findNavController().navigate(R.id.action_addRedirectFragment_pop)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ui.etSource.setOnClickListener {
            registerForNumberPicker.launch()
        }
        ui.etDestination.setOnClickListener {
            registerForNumberPicker2.launch()
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

    private fun extracted() {
        registerForNumberPicker =
            registerForActivityResult(ActivityResultContracts.PickContact()) {
                ContactModelRepository.pickContact(requireContext())?.let {
                    viewModel.onSourceRetrieved(it)
                }
            }
    }

    private fun extracted2() {
        registerForNumberPicker2 =
            registerForActivityResult(ActivityResultContracts.PickContact()) {
                ContactModelRepository.pickContact(requireContext())?.let {
                    viewModel.onDestinationRetrieved(it)
                }
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
            registerForPermissions.launch(missingPermissions.toTypedArray())
        }
    }

    override fun showError(message: Int) {
        Snackbar.make(ui.addredirectContainer, message, Snackbar.LENGTH_LONG).show()
    }

    override fun setRegisterForActivityResult(registerForActivityResult: ActivityResultLauncher<Array<String>>) {
        this.registerForPermissions = registerForActivityResult
    }

    override fun setButtonState(buttonState: ButtonState) {
        when (buttonState) {
            ButtonState.Disabled -> {
                ui.etSource.isEnabled = true
                ui.etDestination.isEnabled = true
                ui.btnAdd.isEnabled = false
                ui.btnAdd.text = getString(R.string.addredirect_info_add)
            }

            ButtonState.Enabled -> {
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
        ui.etSource.setOnClickListener {
            registerForNumberPicker.launch()
        }
        ui.etSource.isFocusableInTouchMode = false
        ui.etSource.clearFocus()
        ui.etSource.inputType = EditorInfo.TYPE_CLASS_PHONE
        hideKeyboardFrom(ui.etSource)
        ui.btnAdvancedMode.setImageResource(R.drawable.ic_regex_24dp)
    }

    private fun setAdvancedMode() {
        ui.etSource.setOnClickListener { }
        ui.etSource.isFocusableInTouchMode = true
        ui.etSource.requestFocus()
        ui.etSource.inputType = EditorInfo.TYPE_CLASS_TEXT
        ui.btnAdvancedMode.setImageResource(R.drawable.ic_regex_24dp)
    }

    private fun hideKeyboardFrom(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
