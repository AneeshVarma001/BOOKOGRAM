package com.example.bookshare.util

import android.content.Context
import android.content.pm.PackageManager

object HardwareUtil {
    /**
     * Detects camera presence in device
     *
     * @param context of the calling activity.
     * @return true, if camera is present in device, else false.
     */
    fun isCameraAvailable(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
}