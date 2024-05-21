package com.pierreduchemin.smsforward.composepres.redirectlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun RedirectListScreen(
    navController: NavController,
    viewModel: CRedirectListViewModel
) {
    val uiState: UiState by viewModel.uiState.collectAsState()


}

@Composable
private fun ClaimDetailContent(uiState: UiState, onBackClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}