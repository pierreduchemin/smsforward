package com.pierreduchemin.smsforward.ui.addredirect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.BuildConfig
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModel
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject

class AddRedirectViewModel(application: Application) : AndroidViewModel(application) {

    val buttonState = MutableLiveData<AddRedirectFragment.ButtonState>()
    val sourceText = MutableLiveData<String>()
    val destinationText = MutableLiveData<String>()
    val errorMessageRes = MutableLiveData<Int>()
    val isComplete = MutableLiveData<Boolean>()
    val isAdvancedModeEnabled = MutableLiveData<Boolean>()

    @Inject
    lateinit var globalModelRepository: GlobalModelRepository

    @Inject
    lateinit var forwardModelRepository: ForwardModelRepository

    private var globalModel: GlobalModel? = null
    private var forwardModel: ForwardModel? = null

    init {
        getApplication<App>().component.inject(this)
        forwardModel = ForwardModel()
        viewModelScope.launch(Dispatchers.IO) {
            globalModel = globalModelRepository.getGlobalModel()
            isAdvancedModeEnabled.postValue(globalModel?.advancedMode)
        }
    }

    fun onSourceRetrieved(source: String?, displayName: String?) {
        if (source == null) {
            errorMessageRes.value = R.string.addredirect_error_invalid_source
            return
        }

        val advancedMode = globalModel?.advancedMode ?: false
        if (!advancedMode) {
            val uSource = PhoneNumberUtils.toUnifiedNumber(getApplication(), source)
            val vSource = PhoneNumberUtils.toVisualNumber(getApplication(), source)
            forwardModel?.from = uSource
            forwardModel?.vfrom = vSource
            if (displayName != null)
                forwardModel?.vfromName = displayName
        }
        notifyUpdate()
    }

    fun onDestinationRetrieved(destination: String?) {
        if (destination == null) {
            errorMessageRes.value = R.string.addredirect_error_invalid_destination
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
            errorMessageRes.value = R.string.addredirect_error_empty_source
            return
        }
        if (destination.isEmpty()) {
            errorMessageRes.value = R.string.addredirect_error_empty_destination
            return
        }
        if (!BuildConfig.DEBUG && localForwardModel.from == localForwardModel.to) {
            errorMessageRes.value =
                R.string.addredirect_error_source_and_redirection_must_be_different
            return
        }
        // TODO: check redirection is not already existing

        val advancedMode = globalModel?.advancedMode ?: false
        if (advancedMode) {
            try {
                Pattern.compile(source)
            } catch (e: PatternSyntaxException) {
                errorMessageRes.value = R.string.addredirect_error_invalid_regex
                return
            }
            forwardModel?.isRegex = true
            forwardModel?.from = source
            forwardModel?.vfrom = source
        }

        runBlocking(Dispatchers.IO) {
            val value = forwardModelRepository.countSameForwardModel(source, destination)
            if (!BuildConfig.DEBUG && value > 0L) {
                errorMessageRes.postValue(R.string.addredirect_error_source_and_redirection_must_be_different)
                return@runBlocking
            }

            viewModelScope.launch(Dispatchers.IO) {
                forwardModelRepository.insertForwardModel(localForwardModel)
                isComplete.postValue(true)
            }
        }
    }

    private fun notifyUpdate() {
        if (forwardModel == null) {
            sourceText.value = ""
            destinationText.value = ""
            buttonState.value = AddRedirectFragment.ButtonState.Disabled
            return
        }

        val localForwardModel = forwardModel!!
        val advancedMode = globalModel?.advancedMode ?: false
        if (!advancedMode) {
            sourceText.value = localForwardModel.vfrom
        }
        destinationText.value = localForwardModel.vto
        val enabled = (advancedMode || localForwardModel.from.isNotEmpty())
                && localForwardModel.to.isNotEmpty()
        if (enabled) {
            buttonState.value = AddRedirectFragment.ButtonState.Enabled
        } else {
            buttonState.value = AddRedirectFragment.ButtonState.Disabled
        }
    }

    fun toggleMode() {
        val enabled = !(isAdvancedModeEnabled.value as Boolean)
        isAdvancedModeEnabled.postValue(enabled)
        viewModelScope.launch(Dispatchers.IO) {
            val currentGlobalModel = globalModel
            if (currentGlobalModel != null) {
                currentGlobalModel.advancedMode = enabled
                globalModelRepository.updateGlobalModel(currentGlobalModel)
            }
        }
    }
}