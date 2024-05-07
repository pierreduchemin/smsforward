package com.pierreduchemin.smsforward.presentation.redirectlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.data.source.database.ForwardModel
import com.pierreduchemin.smsforward.data.source.database.GlobalModel
import com.pierreduchemin.smsforward.utils.NotificationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedirectListViewModel @Inject constructor(
    private val application: Application,
    private val globalModelRepository: GlobalModelRepository,
    private val forwardModelRepository: ForwardModelRepository,
) : ViewModel() {

    companion object {
        private val TAG by lazy { RedirectListViewModel::class.java.simpleName }
    }

    val ldForwardsList = MutableLiveData<List<ForwardModel>>()
    val ldButtonState = MutableLiveData<RedirectListFragment.SwitchState>()

    private var forwardModels: List<ForwardModel> = arrayListOf()
    private var globalModel: GlobalModel? = null

    private val smsReceiver = SmsReceiver()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            forwardModels = forwardModelRepository.getForwardModels()

            globalModel = globalModelRepository.getGlobalModel()
            if (globalModel == null) {
                globalModel = setDefaultGlobalModel()
            }
        }

        globalModelRepository.observeGlobalModel().observeForever {
            if (it == null || !it.activated) {
                NotificationUtils.cancel(application)
                smsReceiver.unregisterSmsReceiver(application)
            } else {
                NotificationUtils.notify(application)
                smsReceiver.registerSmsReceiver(application)
            }
            globalModel = it
        }
        forwardModelRepository.observeForwardModels().observeForever {
            forwardModels = it
            notifyUpdate()
        }
    }

    fun onRedirectionToggled() {
        val localGlobalModel = globalModel!!
        if (localGlobalModel.activated) {
            localGlobalModel.activated = false
            ldButtonState.value = RedirectListFragment.SwitchState.Stopped
            viewModelScope.launch(Dispatchers.IO) {
                globalModelRepository.updateGlobalModel(localGlobalModel)
            }
        } else {
            localGlobalModel.activated = true
            ldButtonState.value = RedirectListFragment.SwitchState.JustEnabled
            viewModelScope.launch(Dispatchers.IO) {
                globalModelRepository.updateGlobalModel(localGlobalModel)
            }
        }
    }

    private fun setDefaultGlobalModel(): GlobalModel {
        val localGlobalModel = GlobalModel(1, activated = false, advancedMode = false)
        viewModelScope.launch(Dispatchers.IO) {
            globalModelRepository.insertGlobalModel(localGlobalModel)
        }
        return localGlobalModel
    }

    private fun notifyUpdate() {
        ldForwardsList.value = forwardModels
        if (forwardModels.isEmpty()) {
            ldButtonState.value = RedirectListFragment.SwitchState.Stopped
            return
        }

        val localActivated = globalModel?.activated ?: false
        if (localActivated) {
            ldButtonState.value = RedirectListFragment.SwitchState.Enabled
        } else {
            ldButtonState.value = RedirectListFragment.SwitchState.Stopped
        }
    }

    fun onDeleteConfirmed(forwardModel: ForwardModel) {
        val id = forwardModel.id
        if (id == null) {
            Log.d(TAG, "Not able to delete Forward Model without id")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            // deactivate redirection if list is going to be empty
            val currentGlobalModel = globalModel
            if (currentGlobalModel != null && forwardModels.size == 1) {
                currentGlobalModel.activated = false
                globalModelRepository.updateGlobalModel(currentGlobalModel)
            }

            forwardModelRepository.deleteForwardModelById(id)
        }
    }
}