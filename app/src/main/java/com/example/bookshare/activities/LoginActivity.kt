package com.example.bookshare.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshare.R
import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.databinding.ActivityLoginBinding
import com.example.bookshare.firebase.FirebaseErrorHandler
import com.example.bookshare.firebase.LoginFirebaseHelper
import com.example.bookshare.firebase.LoginFirebaseHelper.LoginEvents
import com.example.bookshare.models.UserData
import com.example.bookshare.util.BundleUtil
import com.example.bookshare.util.Constants
import com.example.bookshare.util.NetworkUtil
import com.firebase.client.FirebaseError
import com.google.android.material.snackbar.Snackbar

/**
 * This activity helps logging in a user.
 */
class LoginActivity : AppCompatActivity() {
    var binding: ActivityLoginBinding? = null

    /**
     * [UserData] object.
     */
    private var userData: UserData? = null

    /**
     * Firebase error handling class instance.
     */
    private var firebaseErrorHandler: FirebaseErrorHandler? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        /**
         * Get UserData From Bundle
         */
        userData = BundleUtil.getUserDataFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.USER_DATA_KEY,
            UserData("","","")
        )
        /**
         * Firebase error handler
         */
        firebaseErrorHandler =
            FirebaseErrorHandler(findViewById(R.id.main_container_relative_layout))
        if (!NetworkUtil.isNetworkConnected(this)) {
            Snackbar.make(
                findViewById(R.id.main_container_relative_layout),
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    /** Cancel Clicked  */
    fun cancelClicked(v: View?) {
        super.onBackPressed()
    }

    /** signInClicked Clicked  */
    fun signInClicked(v: View?) {
      if(binding!!.nameEditText.visibility == View.VISIBLE){
          binding!!.nameEditText.visibility = View.GONE
          binding!!.txtSignInUp?.text = getString(R.string.sign_in_new_user)
      }else{
          /** Validate all inputs  */
          if (validateEmail() && validatePassword()) {
              /** Show Loading view  */
              binding!!.loadingView.visibility = View.VISIBLE
              binding!!.cardView1.visibility = View.GONE
              /** Correct phone number  */
              userData!!.emailId = binding!!.emailEditText.text.toString()
              userData!!.userName = binding!!.nameEditText.text.toString()
              val loginFirebaseHelper = LoginFirebaseHelper()
              loginFirebaseHelper.setLoginEvents(
                  LoginEventListener(
                      loginFirebaseHelper,
                      binding!!.passwordEditText.text.toString()
                  )
              )
              loginFirebaseHelper.existingLogIn( binding!!.emailEditText.text.toString(), binding!!.passwordEditText.text.toString())

          }
      }
    }

    /**
     * Sign Up Button clicked by user
     *
     * @param v of the button clicked
     */
    fun signUpClicked(v: View?) {
        if(binding!!.nameEditText.visibility == View.GONE){
            binding!!.nameEditText.visibility = View.VISIBLE
            binding!!.txtSignInUp?.text = getString(R.string.sign_up_new_user)
        }else{
        /** Validate all inputs  */
        if (validateName() && validateEmail() && validatePassword()) {
            /** Show Loading view  */
            binding!!.loadingView.visibility = View.VISIBLE
            binding!!.cardView1.visibility = View.GONE
            /** Correct phone number  */
            userData!!.emailId = binding!!.emailEditText.text.toString()
            userData!!.userName = binding!!.nameEditText.text.toString()
            val loginFirebaseHelper = LoginFirebaseHelper()
            loginFirebaseHelper.setLoginEvents(
                LoginEventListener(
                    loginFirebaseHelper,
                    binding!!.passwordEditText.text.toString()
                )
            )
            loginFirebaseHelper.createNewUser(userData!!, binding!!.passwordEditText.text.toString())

        }}
    }

    /**
     * Validate Email
     *
     * @return true, if it is a valid email, else false.
     */
    private fun validateEmail(): Boolean {
        val email = binding!!.emailEditText.text.toString()
        if ((email.isEmpty()
                    || TextUtils.isEmpty(email)
                    || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        ) {
            binding!!.textInputEmail.error = getString(R.string.enter_valid_email_address)
            requestFocus(binding!!.emailEditText)
            return false
        } else {
            binding!!.textInputEmail.isErrorEnabled = false
        }
        return true
    }

    /**
     * Validate Password
     *
     * @return true, if it is a valid password, else false
     */
    private fun validatePassword(): Boolean {
        if (binding!!.passwordEditText.text.toString().trim { it <= ' ' }.isEmpty()) {
            binding!!.textInputPassword.error = getString(R.string.enter_password)
            requestFocus(binding!!.passwordEditText)
            return false
        } else {
            binding!!.textInputPassword.isErrorEnabled = false
        }
        return true
    }

    /**
     * Validate Name
     *
     * @return true, if it is a valid name, else false.
     */
    private fun validateName(): Boolean {
        if (binding!!.nameEditText.text.toString().trim { it <= ' ' }.isEmpty()) {
            binding!!.textInputName.error = getString(R.string.enter_your_name)
            requestFocus(binding!!.nameEditText)
            return false
        } else {
            binding!!.textInputName.isErrorEnabled = false
        }
        return true
    }

    /** Request focus for a view  */
    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    /** Login Event Listener class  */
    internal inner class LoginEventListener(
        var loginFirebaseHelper: LoginFirebaseHelper,
        var password: String
    ) : LoginEvents {
        override fun onUserAuthCheck(isLoggedIn: Boolean, uid: String?) {}
        override fun onUserCreated(isSuccessfullyCreated: Boolean, error: String?) {
            if (isSuccessfullyCreated) {
                loginFirebaseHelper.firstLogIn(userData!!, password)
            } else {
                firebaseErrorHandler!!.processError(error)
                /** Take back loading view  */
                binding!!.loadingView.visibility = View.GONE
                binding!!.cardView1.visibility = View.VISIBLE
            }
        }

        override fun onFirstTimeUserLogIn(isSuccessfullyLoggedIn: Boolean, error: String?) {
            if (isSuccessfullyLoggedIn) {
                /** Set global elements  */
                BookShareApplication.instance!!.setAgentName(userData!!.userName)
                BookShareApplication.instance!!.setEmail(userData!!.emailId)
                BookShareApplication.instance!!.setPassword(password)
                /** Hide few loading related stuff  */
                binding!!.dotsTextView.visibility = View.GONE
                binding!!.loadingTextView.text =
                    String.format(getString(R.string.welcome), "User")
                /** Delay a little, the start of new activity  */
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    /** Start the main Activity  */
                    /** Start the main Activity  */
                    /** Start the main Activity  */
                    /** Start the main Activity  */
                    val intent = Intent(this@LoginActivity, MainScreenActivity::class.java)
                    this@LoginActivity.startActivity(intent)
                    finishAffinity()
                }, 2500)
            } else {
                firebaseErrorHandler!!.processError(error)
                /** Take back laoding view  */
                binding!!.loadingView.visibility = View.GONE
                binding!!.cardView1.visibility = View.VISIBLE
            }
        }

        override fun onExistingUser(isExisting: Boolean, email: String?, password: String?) {}
        override fun onExistingUserLogIn(
            isSuccessfullyLoggedIn: Boolean,
            uid: String?,
            error: String?
        ) {
            if (isSuccessfullyLoggedIn) {
                /** Set global elements  */
//                BookShareApplication.getInstance().agentName = userData!!.userName
                BookShareApplication.instance!!.setAgentName(userData!!.userName)
                BookShareApplication.instance!!.setEmail(userData!!.emailId)
                BookShareApplication.instance!!.setPassword(password)
                /** Hide few loading related stuff  */
                binding!!.dotsTextView.visibility = View.GONE
                binding!!.loadingTextView.text =
                    String.format(getString(R.string.welcome), "User")
                /** Delay a little, the start of new activity  */
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    /** Start the main Activity  */
                    /** Start the main Activity  */
                    /** Start the main Activity  */
                    /** Start the main Activity  */
                    val intent = Intent(this@LoginActivity, MainScreenActivity::class.java)
                    this@LoginActivity.startActivity(intent)
                    finishAffinity()
                }, 2500)
            } else {
                firebaseErrorHandler!!.processError(error)
                /** Take back laoding view  */
                binding!!.loadingView.visibility = View.GONE
                binding!!.cardView1.visibility = View.VISIBLE
            }
        }

        override fun onUserNameFetchedFromExistingUser(userName: String?) {}
        override fun onUserUploadedBookIdFetchedFromExistingUser(bId: String?) {}
    }
}