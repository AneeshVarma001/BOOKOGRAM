package com.example.bookshare.activities

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.bookshare.R
import com.example.bookshare.activityhelpers.SearchActivityHelper
import com.example.bookshare.activityhelpers.SearchActivityHelper.ItemClickEvent
import com.example.bookshare.databinding.ActivitySearchBookBinding
import com.example.bookshare.models.SearchedBook
import com.example.bookshare.util.BookUtil
import com.example.bookshare.util.BundleUtil
import com.example.bookshare.util.Constants

/**
 * Searches book online in Google Books catalogue
 */
class SearchBookActivity : AppCompatActivity() {
    var binding: ActivitySearchBookBinding? = null

    /**
     * Activity helper class instance
     */
    var searchActivityHelper: SearchActivityHelper? = null

    /**
     * Search query string
     */
    var searchedQuery: String? = null

    /**
     * Handler used for waiting for a while before actually
     * searching for a book.
     */
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Book isbn
     */
    var isbn: String? = null

    /**
     * Boolean value denoting whether request came from upload book or not.
     */
    var isFromUpload = false

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBookBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        /**
         * Recycler View Initializations :
         * Make sure that we do not re-initialize if we have done so already.
         * (Helpful when orientation is changed and onCreate is called again)
         */
        if (searchActivityHelper == null) {
            searchActivityHelper = SearchActivityHelper(this)
            searchActivityHelper!!.linkRecyclerViewAndAdapter(binding!!.searchResultRecyclerViewSearchBook)
        }
        /** Listening to item click events  */
        searchActivityHelper!!.setItemClickEvent(RecyclerViewItemClick())
        /** Text change listener  */
        binding!!.searchEditTextSearchBook.doAfterTextChanged {
            /** Need to remove callback  */
            handler.removeCallbacks(mFilterTask)
            /** If length is zero, immediately remove adapter items  */
            if (it != null) {
                if (it.isEmpty()) {
                    if (searchActivityHelper != null) {
                        searchActivityHelper!!.handleEmptyQueryEvent()
                    }
                    searchedQuery = ""
                    return@doAfterTextChanged
                }
            }

            /** Wait one second to get stabled paused input  */
            handler.postDelayed(mFilterTask, 1000)
        }
        /** Check whether the request came from upload books, if this is the case, put the isbn in searched string  */
        isFromUpload = BundleUtil.getBooleanFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.IS_FROM_UPLOAD,
            false
        )
        isbn = BundleUtil.getStringFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.ISBN_KEY,
            ""
        )
        if (isFromUpload) {
            binding!!.searchEditTextSearchBook.setText(isbn)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        /** Saving recycler view's adapter data  */
        if (binding!!.searchResultRecyclerViewSearchBook != null && binding!!.searchResultRecyclerViewSearchBook.layoutManager != null) {
            outState.putParcelable(
                Constants.SAVED_LAYOUT_MANAGER_KEY,
                binding!!.searchResultRecyclerViewSearchBook.layoutManager!!.onSaveInstanceState()
            )
        }
        outState.putBoolean(Constants.IS_FROM_UPLOAD, isFromUpload)
        outState.putString(Constants.ISBN_KEY, isbn)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        /** Restoring recycler view's adapter data  */
        if (savedInstanceState != null && binding!!.searchResultRecyclerViewSearchBook != null && binding!!.searchResultRecyclerViewSearchBook.layoutManager != null) {
            val savedRecyclerLayoutState =
                savedInstanceState.getParcelable<Parcelable>(Constants.SAVED_LAYOUT_MANAGER_KEY)
            binding!!.searchResultRecyclerViewSearchBook.layoutManager!!.onRestoreInstanceState(
                savedRecyclerLayoutState
            )
        }
    }

    override fun onResume() {
        super.onResume()
    }

    /** Class for handling click events  */
    internal inner class RecyclerViewItemClick : ItemClickEvent {
        override fun onItemClicked(transitionImageView: ImageView?, searchedBook: SearchedBook?) {
            val intent = Intent(this@SearchBookActivity, BookDetailActivity::class.java)
            intent.putExtra(Constants.SEARCHED_BOOK_KEY, searchedBook)
            intent.putExtra(Constants.IS_FROM_UPLOAD, isFromUpload)
            val bitmapDrawable = transitionImageView!!.drawable as BitmapDrawable
            intent.putExtra(
                Constants.BOOK_IMAGE_KEY,
                BookUtil.convertBitmapToByteArray(bitmapDrawable.bitmap)
            )
            val pair1 = Pair.create(
                transitionImageView as View,
                getString(R.string.common_image_transition_view)
            )
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this@SearchBookActivity, pair1)
            startActivityForResult(intent, 5, options.toBundle())
        }
    }

    /** Retry demanded by user  */
    fun retryClicked(v: View?) {
        if (searchActivityHelper != null) {
            searchActivityHelper!!.retrySearching()
        }
    }

    /** Filter task runnable  */
    var mFilterTask = Runnable {
        val text = binding!!.searchEditTextSearchBook.text.toString()
        searchActivityHelper!!.handleNewQueryEvent(text)
        searchedQuery = text
    }

    override fun onDestroy() {
        handler.removeCallbacks(mFilterTask)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, requestCode, data)
        if (requestCode == 5 && resultCode == Constants.CLOSE_ACTIVITY) {
            finish()
        }
    }
}