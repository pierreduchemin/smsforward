package com.pierreduchemin.smsforward.redirects

import android.Manifest
import android.app.Activity
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils


class RedirectsPresenter(
    private val activity: Activity,
    private val forwardModelRepository: ForwardModelRepository,
    private val view: RedirectsContract.View
) : RedirectsContract.Presenter {

    companion object {
        private val TAG by lazy { RedirectsPresenter::class.java.simpleName }
    }

    private var smsReceiver: SmsReceiver = SmsReceiver()

    init {
        view.presenter = this
    }

    override fun onViewCreated() {
        val forwardModel = forwardModelRepository.getForwardModel()
        if (forwardModel != null) {
            view.setSource(forwardModel.from)
            view.setDestination(forwardModel.to)
            onNumberPicked()
        }
    }

    override fun onSourceSet(source: String?) {
        if (source == null) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }
        view.setSource(source)

        var forwardModel = forwardModelRepository.getForwardModel()
        forwardModel = if (forwardModel == null) {
            ForwardModel(0, source, "", false)
        } else {
            forwardModel.from = source
            forwardModel
        }
        forwardModelRepository.insertForwardModel(forwardModel)
    }

    override fun onDestinationSet(destination: String?) {
        if (destination == null) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }
        view.setDestination(destination)

        var forwardModel = forwardModelRepository.getForwardModel()
        forwardModel = if (forwardModel == null) {
            ForwardModel(0, "", destination, false)
        } else {
            forwardModel.to = destination
            forwardModel
        }
        forwardModelRepository.insertForwardModel(forwardModel)
    }

    override fun onButtonClicked(source: String, destination: String) {
        if (source.isEmpty()) {
            view.showError(R.string.redirects_error_empty_source)
            return
        }
        if (destination.isEmpty()) {
            view.showError(R.string.redirects_error_empty_destination)
            return
        }

        var forwardModel = forwardModelRepository.getForwardModel()
        if (forwardModel == null) {
            forwardModel = ForwardModel(0, "", "", false)
        }

        if (forwardModel.activated) {
            view.setButtonState(RedirectsFragment.ButtonState.DISABLED)
            view.resetFields()
            forwardModel.activated = false
            // TODO stop service
            activity.unregisterReceiver(smsReceiver)
        } else {
            val fSource = PhoneNumberUtils.toFormattedNumber(activity, source)
            if (fSource == null) {
                view.showError(R.string.redirects_error_invalid_source)
                return
            }
            val fDestination = PhoneNumberUtils.toFormattedNumber(activity, destination)
            if (fDestination == null) {
                view.showError(R.string.redirects_error_invalid_destination)
                return
            }

            if (fSource == fDestination) {
                view.showError(R.string.redirects_error_source_and_redirection_must_be_different)
                return
            }
            if (!view.hasPermission(Manifest.permission.SEND_SMS)) {
                view.askPermission(Manifest.permission.SEND_SMS)
                return
            }

            smsReceiver.setCallback(object : OnSmsReceivedListener {
                override fun onSmsReceived(source: String, message: String) {
                    Log.i(TAG, "Caught a SMS from $source")
                    view.showRedirectMessage(source, destination)
                    sendSMS(
                        destination, activity.getString(
                            R.string.redirects_info_sms_received_from,
                            source,
                            message
                        )
                    )
                }
            })
            smsReceiver.setPhoneNumberFilter(fSource)

            forwardModel.activated = true
            // TODO start service
            activity.registerReceiver(
                smsReceiver,
                IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            )
            view.setButtonState(RedirectsFragment.ButtonState.STOP)
        }

        forwardModelRepository.insertForwardModel(forwardModel)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        Log.i(TAG, "Forwarding to $phoneNumber: $message")
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    override fun onNumberPicked() {
        val forwardModel = forwardModelRepository.getForwardModel()
        if (forwardModel == null) {
            view.setButtonState(RedirectsFragment.ButtonState.DISABLED)
            return
        }

        if (forwardModel.activated) {
            view.setButtonState(RedirectsFragment.ButtonState.STOP)
            return
        }

        val enabled = forwardModel.from.isNotEmpty() && forwardModel.to.isNotEmpty()
        if (enabled) {
            view.setButtonState(RedirectsFragment.ButtonState.ENABLED)
            return
        }

        view.setButtonState(RedirectsFragment.ButtonState.DISABLED)
    }
}