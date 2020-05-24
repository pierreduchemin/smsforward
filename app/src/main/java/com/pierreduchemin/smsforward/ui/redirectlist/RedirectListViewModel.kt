package com.pierreduchemin.smsforward.ui.redirectlist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pierreduchemin.smsforward.App
import com.pierreduchemin.smsforward.data.ForwardModel
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import com.pierreduchemin.smsforward.data.GlobalModel
import com.pierreduchemin.smsforward.data.GlobalModelRepository
import com.pierreduchemin.smsforward.ui.addredirect.RedirectService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RedirectListViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private val TAG by lazy { RedirectListViewModel::class.java.simpleName }
    }

    val ldForwardsList = MutableLiveData<List<ForwardModel>>()
    val ldButtonState = MutableLiveData<RedirectListFragment.SwitchState>()

    @Inject
    lateinit var globalModelRepository: GlobalModelRepository

    @Inject
    lateinit var forwardModelRepository: ForwardModelRepository

    private var forwardModels: List<ForwardModel> = arrayListOf()
    private var globalModel: GlobalModel? = null

    init {
        getApplication<App>().component.inject(this)

        viewModelScope.launch(Dispatchers.IO) {
            forwardModels = forwardModelRepository.getForwardModels()
        }

        globalModelRepository.observeGlobalModel().observeForever {
            globalModel = it
            notifyUpdate()
        }
        forwardModelRepository.observeForwardModels().observeForever {
            forwardModels = it
            notifyUpdate()
        }
    }

    fun onRedirectionToggled() {
        if (globalModel == null) {
            globalModel = setDefaultGlobalModel()
        }

        val localGlobalModel = globalModel!!
        if (localGlobalModel.activated) {
            localGlobalModel.activated = false
            viewModelScope.launch(Dispatchers.IO) {
                globalModelRepository.updateGlobalModel(localGlobalModel)
            }
        } else {
            localGlobalModel.activated = true
            ldButtonState.value = RedirectListFragment.SwitchState.JUSTENABLED
            viewModelScope.launch(Dispatchers.IO) {
                globalModelRepository.updateGlobalModel(localGlobalModel)
            }
        }
    }

    private fun setDefaultGlobalModel(): GlobalModel {
        val localGlobalModel = GlobalModel(1, false)
        viewModelScope.launch(Dispatchers.IO) {
            globalModelRepository.insertGlobalModel(localGlobalModel)
        }
        return localGlobalModel
    }

    private fun notifyUpdate() {
        ldForwardsList.value = forwardModels
        if (forwardModels.isEmpty()) {
            ldButtonState.value = RedirectListFragment.SwitchState.STOP
            return
        }

        val localActivated = globalModel?.activated ?: false
        if (localActivated) {
            ldButtonState.value = RedirectListFragment.SwitchState.ENABLED
            RedirectService.startActionRedirect(getApplication())
        } else {
            ldButtonState.value = RedirectListFragment.SwitchState.STOP
            RedirectService.stopActionRedirect(getApplication())
        }
    }

    fun onDeleteConfirmed(forwardModel: ForwardModel) {
        val id = forwardModel.id
        if (id == null) {
            Log.d(TAG, "Not able to delete Forward Model without id")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            forwardModelRepository.deleteForwardModelById(id)
        }
    }
}