package com.example.bookshare.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bookshare.R
import com.example.bookshare.databinding.ActivityUploadBookBinding
import com.example.bookshare.util.*
import com.google.android.material.snackbar.Snackbar

/**
 * Activity asking for uploading a book.

 */
class UploadBookActivity : AppCompatActivity() {
    var binding: ActivityUploadBookBinding? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBookBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
    }

    /**
     * Tick Clicked by user
     *
     * @param v view for the tick icon
     */
    fun tickClicked(v: View?) {
        if (validateIsbn()) {
            /** call the find book activity with isbn  */
            callSearchBookWithISBN(binding!!.isbnEditText.text.toString())
        }
    }

    /**
     * Back icon pressed by user
     *
     * @param v view for the back icon
     */
    fun backClicked(v: View?) {
        super.onBackPressed()
    }




    /**
     * Validate Isbn
     *
     * @return true, if it is a valid isbn, else false.
     */
    private fun validateIsbn(): Boolean {
        val isbn = binding!!.isbnEditText.text.toString()
        if (isbn.length != 13) {
            binding!!.errorTextView.text = resources.getString(R.string.enter_valid_isbn)
            requestFocus(binding!!.isbnEditText)
            return false
        } else {
            binding!!.errorTextView.text = ""
        }
        return true
    }

    /** Request focus for a view  */
    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /** if isbn scan was requested  */
        if (requestCode == Constants.ISBN_REQUEST) {
            /** Got success  */
            if (resultCode == Constants.RESULT_OK) {
                val isbn =
                    BundleUtil.getStringFromBundle(null, data!!.extras, Constants.ISBN_KEY, "")
                FLog.d(this, isbn)
                /** call the find book activity with isbn  */
                callSearchBookWithISBN(isbn!!)
            } else if (resultCode == Constants.RESULT_FAILURE) {
                /** There was a failure, due to isbn not of a book scanned  */
                Snackbar.make(
                    binding!!.uploadBookMainRelativeLayout,
                    R.string.not_a_book_isbn_scanned,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Calls the Search Book activity with the given isbn.
     *
     * @param isbn of the book
     */
    private fun callSearchBookWithISBN(isbn: String) {
        val intent = Intent(this@UploadBookActivity, SearchBookActivity::class.java)
        intent.putExtra(Constants.ISBN_KEY, isbn)
        intent.putExtra(Constants.IS_FROM_UPLOAD, true)
        startActivity(intent)
        finish()
    }
}