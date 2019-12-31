package com.pierreduchemin.smsforward.redirects

import android.Manifest
import android.app.Activity
import com.google.i18n.phonenumbers.NumberParseException
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

    override fun onSourceRetreived(source: String?) {
        if (source == null) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }

        val fSource = PhoneNumberUtils.toFormattedNumber(activity, source)
        if (fSource == null) {
            view.showError(R.string.redirects_error_invalid_source)
            return
        }
        view.setSource(fSource)

        var forwardModel = forwardModelRepository.getForwardModel()
        forwardModel = if (forwardModel == null) {
            ForwardModel(0, fSource, "", false)
        } else {
            forwardModel.from = fSource
            forwardModel
        }
        forwardModelRepository.insertForwardModel(forwardModel)
    }

    override fun onDestinationRetreived(destination: String?) {
        if (destination == null) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }

        val fDestination = PhoneNumberUtils.toFormattedNumber(activity, destination)
        if (fDestination == null) {
            view.showError(R.string.redirects_error_invalid_destination)
            return
        }
        view.setDestination(fDestination)

        var forwardModel = forwardModelRepository.getForwardModel()
        forwardModel = if (forwardModel == null) {
            ForwardModel(0, "", fDestination, false)
        } else {
            forwardModel.to = fDestination
            forwardModel
        }
        forwardModelRepository.insertForwardModel(forwardModel)
    }

    override fun onButtonClicked(source: String, destination: String) {
        var forwardModel = forwardModelRepository.getForwardModel()
        if (forwardModel == null) {
            forwardModel = ForwardModel(0, "", "", false)
        }

        if (forwardModel.activated) {
            view.setButtonState(RedirectsFragment.ButtonState.DISABLED)
            view.resetFields()
            forwardModelRepository.deleteForwardModelById(forwardModel.id)

            RedirectService.stopActionRedirect(activity)
        } else {
            if (source.isEmpty()) {
                view.showError(R.string.redirects_error_empty_source)
                return
            }
            if (destination.isEmpty()) {
                view.showError(R.string.redirects_error_empty_destination)
                return
            }

            try {
                if (!view.hasPermission(Manifest.permission.SEND_SMS)) {
                    view.askPermission(Manifest.permission.SEND_SMS)
                }

                forwardModel.activated = true
                RedirectService.startActionRedirect(activity)
                view.setButtonState(RedirectsFragment.ButtonState.STOP)

                forwardModelRepository.insertForwardModel(forwardModel)
            } catch (e: NumberParseException) {
                view.showError(R.string.redirects_error_invalid_phone_number)
            }
        }
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