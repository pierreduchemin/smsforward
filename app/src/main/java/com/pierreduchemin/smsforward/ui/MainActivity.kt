package com.pierreduchemin.smsforward.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.databinding.MainActivityBinding


interface PermissionRegisterer {
    fun registerForPermission(permissionSubscriber: PermissionSubscriber)
}

class MainActivity : AppCompatActivity(), PermissionRegisterer {

    companion object {
        private val TAG by lazy { MainActivity::class.java.simpleName }
    }

    private val ui: MainActivityBinding by lazy { MainActivityBinding.inflate(layoutInflater) }

    private lateinit var registerForActivityResult: ActivityResultLauncher<Array<String>>
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        checkForSmsPermission()

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

    private fun checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS),
                MY_PERMISSIONS_REQUEST_SEND_SMS
            )
        } else {
            // Permission already granted. Enable the SMS button.
            Log.d(TAG, "perm ok")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // For the requestCode, check if permission was granted or not.
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                if (permissions[0].equals(Manifest.permission.SEND_SMS, ignoreCase = true)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(TAG, "perm ok 2")
                } else {
                    // Permission denied.
                    Log.d(TAG, "perm nok")
                }
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
