package com.pierreduchemin.smsforward.presentation.addredirect

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pierreduchemin.smsforward.BuildConfig
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.data.ContactModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.data.source.database.ForwardModel
import com.pierreduchemin.smsforward.data.source.database.GlobalModel
import com.pierreduchemin.smsforward.utils.PhoneNumberUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import javax.inject.Inject

@HiltViewModel
class AddRedirectViewModel @Inject constructor() : ViewModel() {

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

    @Inject
    lateinit var application: Application

    private var globalModel: GlobalModel? = null
    private var forwardModel: ForwardModel? = null

    init {
        forwardModel = ForwardModel()
        viewModelScope.launch(Dispatchers.IO) {
            globalModel = globalModelRepository.getGlobalModel()
            isAdvancedModeEnabled.postValue(globalModel?.advancedMode)
        }
    }

    fun onSourceRetrieved(contactModel: ContactModel) {
        val source = contactModel.phoneNo
        val displayName = contactModel.displayName

        if (source == null) {
            errorMessageRes.value = R.string.addredirect_error_invalid_source
            return
        }

        val advancedMode = globalModel?.advancedMode ?: false
        if (!advancedMode) {
            val uSource = PhoneNumberUtils.toUnifiedNumber(application, source)
            val vSource = PhoneNumberUtils.toVisualNumber(application, source)
            forwardModel?.from = uSource
            forwardModel?.vfrom = vSource
            if (displayName != null)
                forwardModel?.vfromName = displayName
        }
        notifyUpdate()
    }

    fun onDestinationRetrieved(contactModel: ContactModel) {
        val destination = contactModel.phoneNo

        if (destination == null) {
            errorMessageRes.value = R.string.addredirect_error_invalid_destination
            return
        }

        val uDestination = PhoneNumberUtils.toUnifiedNumber(application, destination)
        val vDestination = PhoneNumberUtils.toVisualNumber(application, destination)
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