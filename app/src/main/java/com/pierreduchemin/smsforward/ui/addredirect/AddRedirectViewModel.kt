package com.pierreduchemin.smsforward.ui.addredirect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.di.ViewModelModule
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject

class AddRedirectViewModel(application: Application) : AndroidViewModel(application) {

    val buttonState = MutableLiveData<AddRedirectFragment.ButtonState>()
    val sourceText = MutableLiveData<String>()
    val destinationText = MutableLiveData<String>()
    val errorMessageRes = MutableLiveData<Int>()
    val isComplete = MutableLiveData<Boolean>()

    private val forwardModelRepository by inject<ForwardModelRepository>()

    private var forwardModel: ForwardModel? = null

    init {
        KTP.openRootScope()
            .openSubScope(App.APPSCOPE)
            .installModules(ViewModelModule(application))
            .inject(this)

        forwardModel = ForwardModel()
    }

    fun onSourceRetrieved(source: String?) {
        if (source == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_source
            return
        }

        val uSource = PhoneNumberUtils.toUnifiedNumber(getApplication(), source)
        val vSource = PhoneNumberUtils.toVisualNumber(getApplication(), source)
        forwardModel?.from = uSource
        forwardModel?.vfrom = vSource

        notifyUpdate()
    }

    fun onDestinationRetrieved(destination: String?) {
        if (destination == null) {
            errorMessageRes.value = R.string.redirects_error_invalid_destination
            return
        }

        val uDestination = PhoneNumberUtils.toUnifiedNumber(getApplication(), destination)
        val vDestination = PhoneNumberUtils.toVisualNumber(getApplication(), destination)
        forwardModel?.to = uDestination
        forwardModel?.vto = vDestination

        notifyUpdate()
    }

    fun onButtonClicked(source: String, destination: String) {
        val localForwardModel = forwardModel!!
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

        viewModelScope.launch(Dispatchers.IO) {
            forwardModelRepository.insertForwardModel(localForwardModel)
        }
        isComplete.value = true
    }

    private fun notifyUpdate() {
        if (forwardModel == null) {
            sourceText.value = ""
            destinationText.value = ""
            buttonState.value = AddRedirectFragment.ButtonState.DISABLED
            return
        }

        val localForwardModel = forwardModel!!
        sourceText.value = localForwardModel.vfrom
        destinationText.value = localForwardModel.vto
        val enabled = localForwardModel.from.isNotEmpty() && localForwardModel.to.isNotEmpty()
        if (enabled) {
            buttonState.value = AddRedirectFragment.ButtonState.ENABLED
        } else {
            buttonState.value = AddRedirectFragment.ButtonState.DISABLED
        }
    }
}