package com.example.wehearintershipwork.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * check if the require permissions are granted or not
 * */
fun arePermissionsGranted(permissions: Array<String>, context: Context): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        )
            return false
    }
    return true
}

/**
 * request the particular permissions from the user
 * */
fun requestPermissionsCompat(permissions: Array<String>, requestCode: Int, activity: FragmentActivity) {
    ActivityCompat.requestPermissions(activity, permissions, requestCode)

}
fun requestPermissionsCompat(permissions: Array<String>, requestCode: Int, activity: Activity) {
    ActivityCompat.requestPermissions(activity, permissions, requestCode)
}