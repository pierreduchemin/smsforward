package com.pierreduchemin.smsforward.composepres.redirectlist

import com.pierreduchemin.smsforward.data.source.database.ForwardModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UiState(
    var redirectionList: List<ForwardModel> = emptyList()
)

class CRedirectListViewModel {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


}
