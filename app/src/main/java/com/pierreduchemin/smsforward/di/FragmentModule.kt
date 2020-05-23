package com.pierreduchemin.smsforward.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.pierreduchemin.smsforward.ui.redirectlist.RedirectListViewModel
import toothpick.config.Module

class FragmentModule(viewModelStoreOwner: ViewModelStoreOwner) : Module() {

    init {
        val viewModel = ViewModelProvider(viewModelStoreOwner).get(RedirectListViewModel::class.java)
        bind(RedirectListViewModel::class.java).toInstance(viewModel)
    }
}