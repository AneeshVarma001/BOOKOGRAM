package com.example.bookshare.firebase

import android.view.View
import com.example.bookshare.util.FLog.e
import com.google.android.material.snackbar.Snackbar

class FirebaseErrorHandler(var rootView: View) {
    fun processError(firebaseError: String?) {
        firebaseError?.let {
            showSnackBar(it)
            /** Catch the type of exception  */
            e(this, it)
        }

    }

    private fun showSnackBar(mssg: String) {
        Snackbar.make(rootView, mssg, Snackbar.LENGTH_LONG).show()
    }
}