package com.example.bookshare.activities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshare.R
import com.example.bookshare.adapter.MessagesAdapter
import com.example.bookshare.firebase.MessagesFirebaseHelper
import com.example.bookshare.firebase.MessagesFirebaseHelper.MessageFirebaseEvents
import com.example.bookshare.models.Message
import com.example.bookshare.util.BundleUtil
import com.example.bookshare.util.Constants
import com.firebase.client.FirebaseError
import com.google.firebase.database.DatabaseError

/**
 * Messages displaying activity.

 */
class MessagesActivity : AppCompatActivity() {
    /**
     * Email of user.
     */
    var email: String? = null
    var name: String? = null

    /**
     * [MessagesFirebaseHelper] instance.
     */
    var messagesFirebaseHelper: MessagesFirebaseHelper? = null

    /**
     * Recycler view for displaying messages
     */
    var messageRecyclerView: RecyclerView? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        email = BundleUtil.getStringFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.MESSSAGE_EMAIL,
            ""
        )
        name = BundleUtil.getStringFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.NAME,
            ""
        )?:""
        val messageArrayList = ArrayList<Message?>()
        val messagesAdapter =
            MessagesAdapter(R.layout.messages_recycler_view_item, messageArrayList, this,name!!)
        messageRecyclerView = findViewById<View>(R.id.messages_recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true // Scroll to bottom
        messageRecyclerView!!.layoutManager = layoutManager
        messageRecyclerView!!.adapter = messagesAdapter
        messagesFirebaseHelper = MessagesFirebaseHelper()
        messagesFirebaseHelper!!.getAllMessages(email!!)
        messagesFirebaseHelper!!.setMessageFirebaseEvents(object : MessageFirebaseEvents {
            override fun onAllMessagesFetched(
                messageArrayList: ArrayList<Message?>?,
                firebaseError: DatabaseError?
            ) {
                if (firebaseError == null) {
                    messagesAdapter.changeDataCompletely(messageArrayList)
                }
            }

            override fun onAllUnReadMessageCountFetched(
                numberUnreadMessageMap: HashMap<String, Int>?,
                totalCount: Int,
                firebaseError: DatabaseError?
            ) {
            }
        })
        messagesFirebaseHelper!!.markAllMessagesAsRead(email!!)
    }

    /** Back icon clicked by user  */
    fun backClicked(v: View?) {
        super.onBackPressed()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.MESSSAGE_EMAIL, email)
    }

    /** Message send clicked  */
    fun messageSendClicked(v: View?) {
        hideKeyboard()
        val messageEditText = findViewById<View>(R.id.message_edit_text) as EditText
        val message = messageEditText.text.toString()
        if (message.isEmpty()) {
            return
        }
        messageEditText.setText("")
        messageEditText.clearFocus()
        messagesFirebaseHelper!!.sendMessage(email, message)
    }

    /** Hides keyboard  */
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}