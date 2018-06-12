package com.kalk.jmr

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.os.Build
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import java.util.*


val GPS_PERMISSIONS = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

fun hasPermissions(context: Context?, allPermissionNeeded: Array<String>): Boolean {
    for (permission in allPermissionNeeded)
        if (ActivityCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED)
            return false

    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null)
        for (permission in allPermissionNeeded)
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false
    return true
}