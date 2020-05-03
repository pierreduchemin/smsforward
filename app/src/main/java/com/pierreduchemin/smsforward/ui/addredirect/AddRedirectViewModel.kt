package com.pierreduchemin.smsforward.ui.addredirect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.NumberParseException
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.di.ActivityModule
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

class AddRedirectViewModel(application: Application) : AndroidViewModel(application) {

    val buttonState = MutableLiveData<RedirectsFragment.ButtonState>()
    val sourceText = MutableLiveData<String>()
    val destinationText = MutableLiveData<String>()
    val errorMessageRes = MutableLiveData<Int>()

    private val repository: ForwardModelRepository by inject<ForwardModelRepository>()

    private var forwardModel: ForwardModel? = null

    init {
        KTP.openRootScope()
            .openSubScope(App.APPSCOPE)
            .openSubScope(this)
            .installModules(ActivityModule(application))
            .inject(this)

        viewModelScope.launch(Dispatchers.IO) {
            forwardModel = repository.getForwardModel()
        }
    }

    fun onViewCreated() {
        repository.observeForwardModel().observeForever {
            forwardModel = it
            notifyUpdate(forwardModel)
        }
    }

    fun onSourceRetrieved(source: String?) {
        if (source == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_source
            return
        }

        val uSource = PhoneNumberUtils.toUnifiedNumber(getApplication(), source)
        if (uSource == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_source
            return
        }

        val vSource = PhoneNumberUtils.toVisualNumber(getApplication(), source)
        if (vSource == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_source
            return
        }

        var localForwardModel = forwardModel
        localForwardModel = if (localForwardModel == null) {
            ForwardModel(1, uSource, "", vSource, "", false)
        } else {
            localForwardModel.from = uSource
            localForwardModel.vfrom = vSource
            forwardModel
        }
        if (localForwardModel != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertForwardModel(localForwardModel)
            }
        }
    }

    fun onDestinationRetrieved(destination: String?) {
        if (destination == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_destination
            return
        }

        val uDestination = PhoneNumberUtils.toUnifiedNumber(getApplication(), destination)
        if (uDestination == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_destination
            return
        }

        val vDestination = PhoneNumberUtils.toVisualNumber(getApplication(), destination)
        if (vDestination == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_destination
            return
        }

        var localForwardModel = forwardModel
        localForwardModel = if (localForwardModel == null) {
            ForwardModel(1, "", uDestination, "", vDestination, false)
        } else {
            localForwardModel.to = uDestination
            localForwardModel.vto = vDestination
            forwardModel
        }
        if (localForwardModel != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertForwardModel(localForwardModel)
            }
        }
    }

    fun onButtonClicked(source: String, destination: String) {
        if (forwardModel == null) {
            val localForwardModel = ForwardModel(1, "", "", "", "", false)
            forwardModel = localForwardModel
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertForwardModel(localForwardModel)
            }
        }

        var localForwardModel = forwardModel!!
        if (localForwardModel.activated) {
            localForwardModel = ForwardModel(1, "", "", "", "", false)
            notifyUpdate(localForwardModel)
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateForwardModel(localForwardModel)
            }
        } else {
            if (source.isEmpty()) {
                errorMessageRes.value = R.string.redirects_error_empty_source
                return
            }
            if (destination.isEmpty()) {
                errorMessageRes.value = R.string.redirects_error_empty_destination
                return
            }
            if (localForwardModel.from == localForwardModel.to) {
                errorMessageRes.value =
                    R.string.redirects_error_source_and_redirection_must_be_different
                return
            }

            try {
                localForwardModel.activated = true
                buttonState.value = RedirectsFragment.ButtonState.STOP

                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertForwardModel(localForwardModel)
                }
            } catch (e: NumberParseException) {
                errorMessageRes.value = R.string.redirects_error_invalid_phone_number
            }
        }
    }

    fun onNumberPicked() {
        notifyUpdate(forwardModel)
    }

    private fun notifyUpdate(forwardModel: ForwardModel?) {
        if (forwardModel == null) {
            sourceText.value = ""
            destinationText.value = ""
            buttonState.value = RedirectsFragment.ButtonState.DISABLED
            return
        }
        sourceText.value = forwardModel.vfrom
        destinationText.value = forwardModel.vto
        if (forwardModel.activated) {
            buttonState.value = RedirectsFragment.ButtonState.STOP
            RedirectService.startActionRedirect(getApplication())
        } else {
            val enabled = forwardModel.from.isNotEmpty() && forwardModel.to.isNotEmpty()
            if (enabled) {
                buttonState.value = RedirectsFragment.ButtonState.ENABLED
            } else {
                buttonState.value = RedirectsFragment.ButtonState.DISABLED
            }
            RedirectService.stopActionRedirect(getApplication())
        }
    }
}