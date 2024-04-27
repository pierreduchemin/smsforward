package com.pierreduchemin.smsforward.di

import com.pierreduchemin.smsforward.ui.addredirect.AddRedirectViewModel
import com.pierreduchemin.smsforward.ui.addredirect.BootDeviceReceiver
import com.pierreduchemin.smsforward.ui.redirectlist.RedirectListViewModel
import com.pierreduchemin.smsforward.ui.redirectlist.SmsReceiver
import com.pierreduchemin.smsforward.utils.RedirectionManager
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(redirectListViewModel: RedirectListViewModel)
    fun inject(addRedirectViewModel: AddRedirectViewModel)
    fun inject(redirectionManager: RedirectionManager)
    fun inject(bootDeviceReceiver: BootDeviceReceiver)
    fun inject(smsReceiver: SmsReceiver)
}