package com.example.bookshare.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtil {
    /**
     * Checks whether permission is granted or denied.
     *
     * @param context of the calling activity
     * @param permission required by app
     *
     * @return true, if permission is granted, else false
     */
    fun isPermissionGranted(context: Context?, permission: String?): Boolean {
        val permissionValue = ContextCompat.checkSelfPermission(context!!, permission!!)
        return permissionValue == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Given a list of permissions, returns the list of denied permissions
     *
     * @param context of the calling activity
     * @param permissions required by app
     * @return denied permissions list
     */
    fun deniedPermissions(context: Context?, vararg permissions: String): List<String> {
        /** Keeps the list of ungranted permissions  */
        val deniedPermissions: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (!isPermissionGranted(context, permission)) {
                deniedPermissions.add(permission)
            }
        }
        return deniedPermissions
    }

    /**
     * Confirms whether granted permissions result has all permissions granted.
     *
     * @param grantResult is the result received from onRequestPermissionsResult.
     * @return true, if all are granted, else false
     */
    fun confirmGrantedPermissions(grantResult: IntArray): Boolean {
        for (aGrantResult in grantResult) {
            if (aGrantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }
}