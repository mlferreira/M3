package dev.mlferreira.n2eliteunofficial

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


class PermissionController(private val activity: Activity) {

    private val permissionsNeeded = listOf(
//        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.NFC,
//        android.Manifest.permission.INTERNET,
    )

    init {
        val permissionsToRequest = permissionsNeeded.filterNot { checkPermission(it) }
        requestPermissions(permissionsToRequest)
    }

    private fun requestPermissions(permissions: List<String>) {
        if (permissions.isNullOrEmpty()) return
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), 19)
    }

    private fun checkPermission(permission: String): Boolean {
        val hasPermission = ActivityCompat.checkSelfPermission(activity, permission)
        return (hasPermission == PackageManager.PERMISSION_GRANTED)
    }

}