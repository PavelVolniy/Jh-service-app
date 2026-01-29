package com.example.jhserviceapp.presentation.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Permissions {
    const val REQUIRED_PERMISSIONS_CAMERA = Manifest.permission.CAMERA
    const val REQUIRED_PERMISSIONS_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    fun checkPermissionsCamera(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == context.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.CAMERA
            )
        }
    }

    fun checkPermissionsMedia(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == context.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

}