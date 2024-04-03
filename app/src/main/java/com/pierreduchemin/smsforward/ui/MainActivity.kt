package com.pierreduchemin.smsforward.ui

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint

interface PermissionRegisterer {
    fun registerForPermission(permissionSubscriber: PermissionSubscriber)
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionRegisterer {

    private val ui: MainActivityBinding by lazy { MainActivityBinding.inflate(layoutInflater) }

    private lateinit var registerForActivityResult: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val permissionsOk = permissions.entries.all { it.value }
                if (!permissionsOk) {
                    getCurrentFragment()?.let {
                        val fragment = it as? ErrorContract
                        fragment?.showError(R.string.addredirect_warning_permission_refused)
                    } //TODO ?:
                }
            }

    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()

    private fun getCurrentFragment(): Fragment? =
        supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()

    override fun registerForPermission(permissionSubscriber: PermissionSubscriber) {
        permissionSubscriber.setRegisterForActivityResult(registerForActivityResult)
    }
}
