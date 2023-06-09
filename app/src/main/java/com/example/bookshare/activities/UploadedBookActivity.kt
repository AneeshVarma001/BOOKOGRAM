package com.example.bookshare.activities

import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.bookshare.R
import com.example.bookshare.adapter.UploadedBookAdapter
import com.example.bookshare.contentprovider.UploadedBookProvider
import com.example.bookshare.db.UploadedBookTable
import com.example.bookshare.util.Constants
import com.example.bookshare.util.NetworkUtil
import com.google.android.material.snackbar.Snackbar

/**
 * Activity displaying uploaded books by user
 *
 */
class UploadedBookActivity : BaseAbstractActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * uploaded book displaying list view
     */
    var uploadedBookListView: ListView? = null

    /**
     * Adapter for above list view
     */
    var uploadedBookAdapter: UploadedBookAdapter? = null

    /**
     * loading text view
     */
    var loadingTextView: TextView? = null

    /**
     * nothing found text view.
     */
    var nothingFoundTextView: TextView? = null

    /**
     * main relative layout for this activity
     */
    var mainRelativeLayout: RelativeLayout? = null

    /**
     * Called when the activity is starting.
     *
     *
     * This activity extends [BaseAbstractActivity], an abstract activity,
     * which mainly handles permissions grant and navigation drawer related events.
     *
     *
     * Need to call [BaseAbstractActivity.setContentViewAndId], instead
     * of [androidx.appcompat.app.AppCompatActivity.setContentView], because
     * the abstract activity handles all layout inflation by itself, when setContentViewAndId
     * is called with a constant Activity Id for each Activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewAndId(R.layout.activity_uploaded_book, Constants.uploadedBooksActivity)
        loadingTextView = findViewById<View>(R.id.loading_text_view) as TextView
        nothingFoundTextView = findViewById<View>(R.id.nothing_found_text_view) as TextView
        mainRelativeLayout =
            findViewById<View>(R.id.root_container_relative_layout_uploaded_book) as RelativeLayout
        uploadedBookListView = findViewById<View>(R.id.uploaded_book_list_view) as ListView
        /** Show finding.... symbol  */
        if (!NetworkUtil.isNetworkConnected(this)) {
            Snackbar.make(
                mainRelativeLayout!!,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            loadingTextView!!.visibility = View.VISIBLE
            nothingFoundTextView!!.visibility = View.GONE
            /** Loader initialize  */
            loaderManager.initLoader(1, Bundle(), this)
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> {
        return CursorLoader(
            this,
            UploadedBookProvider.CONTENT_URI,
            null,
            null,
            null,
            UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + getString(R.string.desc)
        )
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     *
     * This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader. The Loader will take care of
     * management of its data so you don't have to.
     *
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (data.count == 0) {
            nothingFoundTextView!!.visibility = View.VISIBLE
            loadingTextView!!.visibility = View.GONE
        } else {
            nothingFoundTextView!!.visibility = View.GONE
            loadingTextView!!.visibility = View.GONE
        }
        data.moveToFirst()
        uploadedBookAdapter = UploadedBookAdapter(this, data, R.layout.uploaded_book_list_view_item)
        uploadedBookListView!!.adapter = uploadedBookAdapter
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    override fun onLoaderReset(loader: Loader<Cursor>) {}
}