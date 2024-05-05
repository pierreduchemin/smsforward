package com.pierreduchemin.smsforward.presentation

import androidx.activity.result.ActivityResultLauncher

interface PermissionSubscriber {
    fun setRegisterForActivityResult(registerForActivityResult: ActivityResultLauncher<Array<String>>)
    fun hasPermission(permissionString: String): Boolean
    fun askPermission(permissionsString: Array<String>)
}