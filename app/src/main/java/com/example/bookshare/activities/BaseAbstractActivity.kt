package com.example.bookshare.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.example.bookshare.R
import com.example.bookshare.alertDialogs.AlertMessageDialog
import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.databinding.ActivityBaseBinding
import com.example.bookshare.firebase.LoginFirebaseHelper
import com.example.bookshare.firebase.MessagesFirebaseHelper
import com.example.bookshare.firebase.MessagesFirebaseHelper.MessageFirebaseEvents
import com.example.bookshare.models.Message
import com.example.bookshare.util.Constants
import com.example.bookshare.util.PermissionUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * This is an abstract activity, which mainly handles permissions grant and
 * navigation drawer related events.
 */
abstract class BaseAbstractActivity : AppCompatActivity() {
    private var binding: ActivityBaseBinding? = null

    /**
     * Boolean value denoting drawer open state.
     */
    private var isDrawerOpen = false

    /**
     * Activity Id corresponding to each extending activity. <br></br>
     * This id is used by abstract base activity to identify which activity is calling it.
     */
    var activityId = 0

    /**
     * HashMap mapping friend's name in String to unread message count in Integer.
     */
    @JvmField
    var numberUnreadCountMap: HashMap<String, Int>? = null

    /**
     * Event for message count changed.
     */
    @JvmField
    var messageCountChangeEvent: MessageCountChangeEvent? = null

    /**
     * Called when the activity is starting.
     *
     *
     * All extending Activities need to call [BaseAbstractActivity.setContentViewAndId],
     * instead of [androidx.appcompat.app.AppCompatActivity.setContentView], because
     * this activity handles all layout inflation by itself, when setContentViewAndId
     * is called with a constant Activity Id for each Activity.
     *
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        /**
         * Set the main Content View.
         */
        setContentView(binding!!.root)
        /**
         * Need to ask for permissions for android M and above
         */
        confirmAllPermissions()
        /**
         * Drawer Listener
         */
        isDrawerOpen = false
        binding!!.drawerLayout.setDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                isDrawerOpen = true
            }

            override fun onDrawerClosed(drawerView: View) {
                isDrawerOpen = false
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
        /**
         * Get all unread messages count and set in the message row in the drawer
         */
        val messagesFirebaseHelper = MessagesFirebaseHelper()
        /**
         * Call all unread message count method in firebase message helper.
         */
        messagesFirebaseHelper.allUnreadMessageCount
        /** Show User Information*/
        FirebaseDatabase.getInstance().getReference(Constants.USERs).child(BookShareApplication.instance!!.uid!!).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mapHashMap = snapshot.value as HashMap<String, String>?
                    if(mapHashMap!=null){
                        BookShareApplication.instance!!.setAgentName(mapHashMap["Name"])

                        binding!!.agentNameTextView.text = BookShareApplication.instance!!.getAgentName()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        binding!!.agentNameTextView.text = BookShareApplication.instance!!.getAgentName()
        binding!!.agentEmailTextView.text = BookShareApplication.instance!!.getEmail()
        /**
         * Listen to message Firebase events.
         */
        messagesFirebaseHelper.setMessageFirebaseEvents(object : MessageFirebaseEvents {
            override fun onAllMessagesFetched(
                messageArrayList: ArrayList<Message?>?,
                firebaseError: DatabaseError?
            ) {
            }

            override fun onAllUnReadMessageCountFetched(
                numberUnreadMessageMap: HashMap<String, Int>?,
                totalCount: Int,
                firebaseError: DatabaseError?
            ) {
                /**
                 * No error received.
                 * Need not handle error received case, because message count won't get updated,
                 * which is the expected behaviour.
                 */
                if (firebaseError == null) {
                    if (totalCount > 0) {
                        /**
                         * If message counter is more than zero, then make counter visible.
                         */
                        binding!!.messsageCounterRelativeLayout.visibility = View.VISIBLE
                        binding!!.messageCountTextView.text = totalCount.toString()
                    } else {
                        /**
                         * Else set visibility to GONE.
                         */
                        binding!!.messsageCounterRelativeLayout.visibility = View.GONE
                    }
                    /**
                     * Initialize new map received from unread message count event.
                     */
                    numberUnreadCountMap = HashMap(numberUnreadMessageMap)
                    /**
                     * Notify message count change event.
                     */
                    if (messageCountChangeEvent != null) {
                        messageCountChangeEvent!!.notifyMessageCountChange()
                    }
                }
            }
        })
        /**
         * Default activity id
         */
        activityId = -1
    }

    /**
     * Message Count Change Event Notifier.
     * Implemented by extending activities for notification of such event.
     */
    interface MessageCountChangeEvent {
        /**
         * Called whenever message count has changed.
         */
        fun notifyMessageCountChange()
    }

    /**
     * Inflates given layout id in its main content frame layout.
     * Also, activity Id is required to identify extending activity.
     * This method must be called by every extending activity.
     *
     * @param layoutId layout to be inflated in the main content frame
     * @param activityId this id has to be unique for every extending activity.
     */
    fun setContentViewAndId(layoutId: Int, activityId: Int) {
        /**
         * Get the layout inflater
         */
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        /**
         * Get the main frame layout
         */
        val frameLayout = findViewById<View>(R.id.main_frame_layout) as FrameLayout
        /**
         * Inflate the given layout
         */
        layoutInflater.inflate(layoutId, frameLayout, true)
        /**
         * Activity id
         */
        this.activityId = activityId
    }

    /**
     * Toggle Drawer's state.
     * Capture click events on menu icon from child activities.
     *
     * @param v view used to toggle drawer state.
     */
    fun toggleDrawer(v: View?) {
        if (isDrawerOpen) {
            closeDrawer()
        } else {
            openDrawer()
        }
        isDrawerOpen = !isDrawerOpen
    }

    /**
     * Opens the drawer.
     */
    private fun openDrawer() {
        binding!!.drawerLayout.openDrawer(GravityCompat.START)
    }

    /**
     * Closes the drawer.
     */
    private fun closeDrawer() {
        binding!!.drawerLayout.closeDrawer(GravityCompat.START)
    }

    /**
     * Home drawer item clicked
     *
     * @param v view for drawer item
     */
    fun homeClicked(v: View?) {
        handleItemClicked(Constants.homeDrawerItemCode)
    }

    /**
     * Uploaded Books drawer item clicked
     *
     * @param v view for drawer item
     */
    fun uploadedBooksClicked(v: View?) {
        handleItemClicked(Constants.uploadedBooksDrawerItemCode)
    }

    /**
     * Logout drawer item clicked
     *
     * @param v view for drawer item
     */
    fun logoutClicked(v: View?) {
        handleItemClicked(Constants.logoutDrawerItemCode)
    }

    /**
     * Messages drawer item clicked
     *
     * @param v view for drawer item
     */
    fun messagesClicked(v: View?) {
        handleItemClicked(Constants.messagesDrawerItemCode)
    }

    /**
     * About drawer item clicked
     *
     * @param v view for drawer item
     */
    fun aboutClicked(v: View?) {
        handleItemClicked(Constants.aboutDrawerItemCode)
    }

    /**
     * Handler for every drawer item click
     *
     * @param drawerItemCode it is the code given to each drawer item.
     */
    fun handleItemClicked(drawerItemCode: Int) {
         if (drawerItemCode == Constants.logoutDrawerItemCode) {
            val f = LoginFirebaseHelper()
            f.setLogoutEvents(object : LoginFirebaseHelper.LogOutEvents {
                override fun logOut() {
                    BookShareApplication.instance!!.setEmail("")
                    BookShareApplication.instance!!.setPassword("")
                    startActivity(Intent(this@BaseAbstractActivity,LoginActivity::class.java))
                    finishAffinity()
                }
            })
            f.logout()
        }else
        if (activityId == Constants.homeActivity) {
            toggleDrawer(null)
            if (drawerItemCode != Constants.homeDrawerItemCode) {
                startCorrespondingActivity(drawerItemCode)
            }
        } else {
            if (activityId == drawerItemCode) {
                toggleDrawer(null)
            } else if (drawerItemCode == Constants.homeDrawerItemCode) {
                finish()
            } else {
                startCorrespondingActivity(drawerItemCode)
                finish()
            }
        }
    }

    /**
     * Start the activity corresponding to drawer item code given
     *
     * @param drawerItemCode it is the code given to each drawer item
     */
    fun startCorrespondingActivity(drawerItemCode: Int) {
        var intent: Intent? = null
        when (drawerItemCode) {
            Constants.homeDrawerItemCode -> intent = Intent(this, MainScreenActivity::class.java)
            Constants.uploadedBooksDrawerItemCode -> intent =
                Intent(this, UploadedBookActivity::class.java)
            Constants.messagesDrawerItemCode -> intent =
                Intent(this, MessagesNamesActivity::class.java)
            Constants.aboutDrawerItemCode -> intent = Intent(this, AboutActivity::class.java)
        }
        intent?.let { startActivity(it) }
    }

    /**
     * This captures navigation header click events,
     * this does nothing, just capture click events,
     * if this event is not captured, the click gets
     * passed to parent activity, which was not the
     * required behaviour.
     *
     * @param v view of the navigation header
     */
    fun navigationDrawerHeaderClicked(v: View?) {
        // Nothing to be done
    }

    /**
     * Captures back presses, if drawer is open, closes
     * the drawer first and then if again back is pressed
     * with drawer closed, it closes the corresponding
     * activity
     */
    override fun onBackPressed() {
        if (isDrawerOpen) {
            toggleDrawer(null)
        } else {
            super.onBackPressed()
        }
    }

    /**
     * This function confirms that all permissions required to run
     * this app are present, if not, ask for permissions.
     */
    private fun confirmAllPermissions() {
        /**
         * Get list of not granted permissions
         */
        val deniedPermissions = PermissionUtil.deniedPermissions(
            this,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        /**
         * if all the permissions granted, nothing needs to be done
         */
        if (deniedPermissions.isEmpty()) {
            return
        }
        /**
         * Request for permissions
         */
        ActivityCompat.requestPermissions(
            this,
            deniedPermissions.toTypedArray(),
            Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        /**
         * if request code matches the request code for asking permissions, confirm that all permissions are granted
         */
        if (requestCode == Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS) {
            if (!PermissionUtil.confirmGrantedPermissions(grantResults)) {
                needPermissionsDialog()
            }
        }
    }

    /**
     * Permissions denied by user, ask him to allow again or close the app
     */
    fun needPermissionsDialog() {
        /**
         * Alert Message Dialog initialization.
         */
        val alertMessageDialog =
            AlertMessageDialog(
                this,
                getString(R.string.permissions_denied),
                getString(R.string.go_to_app_settings),
                getString(R.string.cancel),
                getString(R.string.app_settings)
            )
        /**
         * Show the alert dialog
         */
        alertMessageDialog.show()
        /**
         * capture click events
         */
        alertMessageDialog.setOnAlertButtonClicked(object :
            AlertMessageDialog.OnAlertButtonClicked {
            override fun onLeftButtonClicked(v: View?) {
                finish()
            }

            override fun onRightButtonClicked(v: View?) {
                /**
                 * Take user to app settings
                 */
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(getString(R.string.package_string), packageName, null)
                intent.data = uri
                startActivity(intent)
                finish()
            }
        })
    }
}