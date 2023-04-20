package com.example.bookshare.activities

import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.bookshare.R
import com.example.bookshare.adapter.MessagesNamesAdapter
import com.example.bookshare.firebase.RPNFirebaseHelper
import com.example.bookshare.models.MessageNames
import com.example.bookshare.models.MessageNames.MessageNamesBuilder
import com.example.bookshare.util.Constants
import com.example.bookshare.util.NetworkUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseError

/**
 * This activity list all friend's who can be messaged using this app.

 */
class MessagesNamesActivity : BaseAbstractActivity() {
    /**
     * List view displaying contacts available for messaging.
     */
    var messagesNamesListView: ListView? = null

    /**
     * Adapter for list view.
     */
    var messagesNamesAdapter: MessagesNamesAdapter? = null

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
        setContentViewAndId(R.layout.activity_messages_names, Constants.messagesActivity)
        /**
         * Initialize views.
         */
        loadingTextView = findViewById<View>(R.id.loading_text_view) as TextView
        nothingFoundTextView = findViewById<View>(R.id.nothing_found_text_view) as TextView
        mainRelativeLayout =
            findViewById<View>(R.id.relative_layout_massages_names) as RelativeLayout
        messagesNamesListView = findViewById<View>(R.id.messages_list_view) as ListView
        /**
         * Notify adapter on message counter changed
         */
        messageCountChangeEvent = object : MessageCountChangeEvent {
            override fun notifyMessageCountChange() {
                if (messagesNamesAdapter != null) {
                    messagesNamesAdapter!!.updateNumberUnreadCountMap(numberUnreadCountMap)
                }
            }
        }
        /**
         * If internet connectivity present, show loading icon and start
         * loading data, else, show no internet connection snack bar.
         */
        if (NetworkUtil.isNetworkConnected(this)) {
            loadingTextView!!.visibility = View.VISIBLE
            nothingFoundTextView!!.visibility = View.GONE
            /** Loader initialize  */
            getDataFromFirebase()
        } else {
            Snackbar.make(
                mainRelativeLayout!!,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }


    /**
     * Fetches rpn data from firebase,
     * only authenticated user can do this, and can only read those rpns
     * which are in his connections.
     *
     * @param data cursor for loaded connections
     */
    fun getDataFromFirebase() {
        val rpnFirebaseHelper = RPNFirebaseHelper()
        rpnFirebaseHelper.fetchListOfRPN()
        rpnFirebaseHelper.setRpnEvents(object : RPNFirebaseHelper.RPNEvents {
            override fun onRPNFetched(
                rpnList: List<HashMap<String, String>>?,
                firebaseError: DatabaseError?
            ) {
                var rpnData = ArrayList<HashMap<String, String>>()
                if (firebaseError == null) {
                    rpnData = ArrayList(rpnList)
                }
                val messageNamesArrayList = createMessageNamesList(rpnData)
                /**
                 * Initialize connections adapter, since data is now available for it.
                 */
                /**
                 * Initialize connections adapter, since data is now available for it.
                 */
                messagesNamesAdapter = MessagesNamesAdapter(
                    this@MessagesNamesActivity,
                    messageNamesArrayList,
                    R.layout.messages_names_list_view_item,
                    numberUnreadCountMap
                )
                loadingTextView!!.visibility = View.GONE
                if (messageNamesArrayList.isEmpty()) {
                    nothingFoundTextView!!.visibility = View.VISIBLE
                } else {
                    nothingFoundTextView!!.visibility = View.GONE
                }
                /**
                 * Set on item click event.
                 */
                /**
                 * Set on item click event.
                 */
                messagesNamesAdapter!!.setMessagesNamesItemClickEvent(object :
                    MessagesNamesAdapter.MessagesNamesItemClickEvent {
                    override fun onItemClicked(v: View?, position: Int) {

                        val intent =
                            Intent(this@MessagesNamesActivity, MessagesActivity::class.java)
                        intent.putExtra(
                            Constants.MESSSAGE_EMAIL,
                            messageNamesArrayList[position].email
                        )
                        intent.putExtra(
                            Constants.NAME,
                            messageNamesArrayList[position].name
                        )
                        val pair1 = Pair.create(
                            findViewById<View>(R.id.fake_action_bar),
                            getString(R.string.fake_action_bar_transition)
                        )
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MessagesNamesActivity,
                            pair1
                        )
                        startActivity(intent, options.toBundle())
                    }
                })
                messagesNamesListView!!.adapter = messagesNamesAdapter
            }
        })
    }

    /**
     * Called after [.onRestoreInstanceState], [.onRestart], or
     * [.onPause], for activity to start interacting with the user.
     */
    public override fun onResume() {
        super.onResume()
        if (messagesNamesAdapter != null) {
            messagesNamesAdapter!!.updateNumberUnreadCountMap(numberUnreadCountMap)
        }
    }

    /**
     * Create [MessageNames] domain object.
     * Requires cursor data and list of rpns.
     *
     * @param rpnData list of rpn strings
     * @return list of [MessageNames] objects.
     */
    private fun createMessageNamesList(
        rpnData: ArrayList<HashMap<String, String>>
    ): ArrayList<MessageNames> {
        val messageNamesArrayList = ArrayList<MessageNames>()
        for (i in 0 until rpnData.count()) {
            var email = rpnData[i]["email"]
            var name = rpnData[i]["name"]
            email = email!!.replace("_dot_".toRegex(), ".")
            email = email.replace("_at_the_rate_".toRegex(), "@")
            val messagesNames = MessageNamesBuilder()
                .addBitmap(null)
                .addName(name)
                .addEmail(email)
                .build()
            messageNamesArrayList.add(messagesNames)
        }
        return messageNamesArrayList
    }
}