package com.example.bookshare.util

import android.util.Log

/**
 * Wrapper util class over `android.util.Log`.
 */
object FLog {
    /** Switch for showing logs  */
    private const val showLog = true
    @JvmStatic
    fun d(ob: Any, mssg: String?) {
        if (showLog) {
            Log.d(makeLogNameSpace(ob) + " : ", mssg!!)
        }
    }

    fun w(ob: Any, mssg: String?) {
        if (showLog) {
            Log.w(makeLogNameSpace(ob) + " : ", mssg!!)
        }
    }

    @JvmStatic
    fun i(ob: Any, mssg: String?) {
        if (showLog) {
            Log.i(makeLogNameSpace(ob) + " : ", mssg!!)
        }
    }

    @JvmStatic
    fun e(ob: Any, mssg: String?) {
        if (showLog) {
            Log.e(makeLogNameSpace(ob) + " : ", mssg!!)
        }
    }

    private fun makeLogNameSpace(ob: Any): String {
        return "Fiboku => " + ob.javaClass.name
    }
}