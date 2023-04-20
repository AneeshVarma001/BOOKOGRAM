package com.example.bookshare.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshare.R
import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.firebase.LoginFirebaseHelper
import com.example.bookshare.firebase.LoginFirebaseHelper.LoginEvents
import com.google.firebase.FirebaseApp

/**
 * Splash displaying activity
 */
class SplashActivity : AppCompatActivity() {
/**
 * Timer for splash screen*/
    val timerInMiliseconds = 4500L

    /**
     * Runnable for handler for logo animation
     */
    private var logoRunnable: Runnable? = null

    /**
     * handler for login activity startup
     */
    private var loginHandler: Handler? = null

    /**
     * Runnable for handler for login activity startup
     */
    private var loginRunnable =  Runnable {

        /** Check user login **/
        val loginFirebaseHelper =  LoginFirebaseHelper()
        loginFirebaseHelper.checkAuthentication()
        loginEventListener =  LoginEventListener(loginFirebaseHelper)
        loginFirebaseHelper.setLoginEvents(loginEventListener)
    }


    /**
     * Listening to login events
     */
    private var loginEventListener: LoginEventListener? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseApp.initializeApp(applicationContext)
        Companion.isDestroyed = false
        loginHandler =  Handler(Looper.getMainLooper())

        loginHandler!!.postDelayed(loginRunnable, timerInMiliseconds)

}

    /** Listening login events  */
    internal inner class LoginEventListener(private val loginFirebaseHelper: LoginFirebaseHelper) :
        LoginEvents {
        override fun onUserAuthCheck(isLoggedIn: Boolean, uid: String?) {
            /** Auth check done, check whether user is already logged in or not  */
            if (isLoggedIn) {
                /** Already logged in  */
                if (!Companion.isDestroyed) {
                    /** Start the main Activity  */
                    val intent = Intent(this@SplashActivity, MainScreenActivity::class.java)
                    this@SplashActivity.startActivity(intent)
                }
                finish()
            } else {
                /** Not logged in  */
                val email = BookShareApplication.instance!!.getEmail()
                val password = BookShareApplication.instance!!.getPassword()
                if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                    /** Its first login  */
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    this@SplashActivity.startActivity(intent)
                    finish()
                } else {
                    /** There has been a token expiry  */
                    loginFirebaseHelper.existingLogIn(email, password)
                }
            }
        }

        override fun onUserCreated(isSuccessfullyCreated: Boolean, error: String?) {}
        override fun onFirstTimeUserLogIn(isSuccessfullyLoggedIn: Boolean, error: String?) {}
        override fun onExistingUser(isExisting: Boolean, email: String?, password: String?) {}
        override fun onExistingUserLogIn(
            isSuccessfullyLoggedIn: Boolean,
            uid: String?,
            error: String?
        ) {
            if (isSuccessfullyLoggedIn) {
                /** Start the main Activity  */
                val intent = Intent(this@SplashActivity, MainScreenActivity::class.java)
                this@SplashActivity.startActivity(intent)
                finish()
            } else {
                /** Its first login  */
                val intent =
                    Intent(this@SplashActivity, LoginActivity::class.java)
                this@SplashActivity.startActivity(intent)
                finish()
            }
        }

        override fun onUserNameFetchedFromExistingUser(userName: String?) {}
        override fun onUserUploadedBookIdFetchedFromExistingUser(bId: String?) {}
    }

    public override fun onDestroy() {
       loginHandler!!.removeCallbacks(loginRunnable!!)
        loginEventListener = null
        Companion.isDestroyed = true
        super.onDestroy()
    }

    companion object {
        /**
         * Boolean value denoting activity's lifecycle end
         */
        private var isDestroyed = false
    }
}