package com.example.bookshare.activities

import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshare.R
import com.example.bookshare.adapter.FindBookInFriendAdapter
import com.example.bookshare.firebase.FindBookFirebaseHelper
import com.example.bookshare.firebase.FindBookFirebaseHelper.FindBookFirebaseCompletionEvents
import com.example.bookshare.firebase.FindBookFirebaseHelper.FindBookFirebaseEvents
import com.example.bookshare.firebase.RPNFirebaseHelper
import com.example.bookshare.firebase.RPNFirebaseHelper.RPNEvents
import com.example.bookshare.models.FindBookMetaData
import com.example.bookshare.util.BundleUtil
import com.example.bookshare.util.Constants
import com.example.bookshare.util.FLog
import com.example.bookshare.util.NetworkUtil
import com.firebase.client.FirebaseError
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseError

/**
 * This activity searches for a given book in all friend's that user has.
 * Even in friend's friend's list too.
 *
 */
class FindBookInFriendActivity : AppCompatActivity(){
    /**
     * The book to be searched for
     */
    var lookupBook: String? = null

    /**
     * List view for displaying friend's name who has the book
     */
    var findBookInFriendListView: ListView? = null

    /**
     * Adapter for above list view.
     */
    var findBookInFriendAdapter: FindBookInFriendAdapter? = null

    /**
     * Loading text view shown when loading of data is going on in background.
     */
    var loadingTextView: TextView? = null

    /**
     * Nothing found text view shown when no data is found.
     */
    var nothingFoundTextView: TextView? = null

    /**
     * Main relative layout for this activity.
     */
    var mainRelativeLayout: RelativeLayout? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_book_in_friend)
        /**
         * Initialize views.
         */
        loadingTextView = findViewById<View>(R.id.loading_text_view) as TextView
        nothingFoundTextView = findViewById<View>(R.id.nothing_found_text_view) as TextView
        mainRelativeLayout =
            findViewById<View>(R.id.root_container_relative_layout) as RelativeLayout
        lookupBook = BundleUtil.getStringFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.SEARCHED_BOOK_TITLE,
            ""
        )
        findBookInFriendListView = findViewById<View>(R.id.friend_list_view) as ListView
        /**
         * If internet connectivity present, show loading icon and start
         * loading data, else, show no internet connection snack bar.
         */
        if (!NetworkUtil.isNetworkConnected(this)) {
            Snackbar.make(
                (mainRelativeLayout)!!,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            loadingTextView!!.visibility = View.VISIBLE
            nothingFoundTextView!!.visibility = View.GONE
            /** Loader initialize  */
            getDataFromFirebase()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.SEARCHED_BOOK_TITLE, lookupBook)
    }


    /**
     * Fetches rpn data from firebase,
     * only authenticated user can do this, and can only read those rpns
     * which are in his connections.

     */
    fun getDataFromFirebase() {
        val rpnFirebaseHelper = RPNFirebaseHelper()
        /**
         * Fetch list of rpns
         */
        rpnFirebaseHelper.fetchListOfRPN()
        rpnFirebaseHelper.setRpnEvents(object :RPNEvents{
            override fun onRPNFetched(
                rpnList: List<HashMap<String, String>>?,
                firebaseError: DatabaseError?
            ) {
            var rpnData = ArrayList<HashMap<String,String>>()
            if (firebaseError == null) {
                rpnData = ArrayList(rpnList)
            }
            val findBookMetaDataArrayList = createFindBookMetaDataList(rpnData)
            val findBookFirebaseHelper = FindBookFirebaseHelper()
                loadingTextView!!.visibility = View.GONE
                if(rpnData.isEmpty()){
                    nothingFoundTextView!!.visibility = View.VISIBLE}else{
                    nothingFoundTextView!!.visibility = View.GONE
                }
            /**
             * Need to fetch list of all meta data for books.
             */
            /**
             * Need to fetch list of all meta data for books.
             */
            findBookFirebaseHelper.completeFindBookMetaDataList(findBookMetaDataArrayList)
            findBookFirebaseHelper.setFindBookFirebaseEvents(object : FindBookFirebaseEvents {
                override fun onAllFindBookMetaDataFetched(findBookMetaDatum: ArrayList<FindBookMetaData?>?) {
                    FLog.d(this, findBookMetaDatum.toString())
                    val adapterList = ArrayList<FindBookMetaData>()
                    findBookFirebaseHelper.setFindBookFirebaseCompletionEvents(
                      object:  FindBookFirebaseCompletionEvents {  override fun onSearchComplete(
                          isSuccessful: Boolean,
                          findBookMetaData: FindBookMetaData?
                      ) { if (isSuccessful) {
                          if (findBookMetaData != null) {
                              adapterList.add(findBookMetaData)
                          }

                                if (findBookInFriendAdapter == null) {
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    /**
                                     * Initialize connections adapter, since data is now available for it.
                                     */
                                    findBookInFriendAdapter = FindBookInFriendAdapter(
                                        this@FindBookInFriendActivity,
                                        adapterList,
                                        R.layout.find_book_in_friend_view_item,
                                        lookupBook!!
                                    )
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    /**
                                     * Update the list view with the adapter.
                                     */
                                    findBookInFriendListView!!.adapter = findBookInFriendAdapter
                                } else {
                                    /**
                                     * update adapter
                                     */
                                    /**
                                     * update adapter
                                     */
                                    /**
                                     * update adapter
                                     */
                                    /**
                                     * update adapter
                                     */
                                    /**
                                     * update adapter
                                     */
                                    /**
                                     * update adapter
                                     */
                                    /**
                                     * update adapter
                                     */
                                    /**
                                     * update adapter
                                     */
                                    findBookInFriendAdapter!!.updateAll(adapterList)
                                }

                          }
                      }})
                    /**
                     * For each meta book data, find look up book.
                     */
                    /**
                     * For each meta book data, find look up book.
                     */
                    if (findBookMetaDatum != null) {
                        for (findBookMetaData: FindBookMetaData? in findBookMetaDatum) {
                            lookupBook?.let {
                                if (findBookMetaData != null) {
                                    findBookFirebaseHelper.findBook(it, findBookMetaData)
                                }
                            }
                        }
                    }
                }
            })
        }  })
    }

    /**
     * Create [FindBookMetaData] domain object.
     * Requires list of rpns.
     * @param rpnData list of rpn strings
     * @return list of [FindBookMetaData] objects.
     */
    private fun createFindBookMetaDataList(
        rpnData: ArrayList<HashMap<String,String>>
    ): ArrayList<FindBookMetaData> {
        val findBookMetaDataArrayList = ArrayList<FindBookMetaData>()
        for (i in 0 until rpnData.count()) {
            var email = rpnData[i]["email"]
            val name = rpnData[i]["name"]
            email = email!!.replace("_dot_".toRegex(), ".")
            email = email.replace("_at_the_rate_".toRegex(), "@")
            findBookMetaDataArrayList.add(FindBookMetaData(name, email, ""))
        }
        return findBookMetaDataArrayList
    }

    /**
     * Back icon pressed by user.
     *
     * @param v view of the back icon.
     */
    fun backClicked(v: View?) {
        super.onBackPressed()
    }
}