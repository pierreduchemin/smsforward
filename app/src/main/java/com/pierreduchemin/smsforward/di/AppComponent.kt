package com.pierreduchemin.smsforward.di

import com.pierreduchemin.smsforward.presentation.addredirect.AddRedirectViewModel
import com.pierreduchemin.smsforward.presentation.redirectlist.RedirectListViewModel
import com.pierreduchemin.smsforward.presentation.redirectlist.RedirectService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(redirectListViewModel: RedirectListViewModel)
    fun inject(addRedirectViewModel: AddRedirectViewModel)
    fun inject(redirectViewModel: RedirectService)
}