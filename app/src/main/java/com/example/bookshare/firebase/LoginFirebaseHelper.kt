package com.example.bookshare.firebase

import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.models.UserData
import com.example.bookshare.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginFirebaseHelper {
    //    var loginEvents: LoginEvents? = null
    var firebase = FirebaseAuth.getInstance()

    //    var firebase: Firebase = Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK)
    private var loginEventListener: LoginEvents? = null
    private var logOutEventListener: LogOutEvents? = null

    fun checkAuthentication() {
        /** Check user's logged in status  */
        firebase.addAuthStateListener(FirebaseAuth.AuthStateListener { authData ->
            /** If callback is not present, return  */
            /** If callback is not present, return  */
            if (loginEventListener == null) {
                return@AuthStateListener
            }
            /** authData decides user logged in state  */
            /** authData decides user logged in state  */
            authData.uid?.let {
                loginEventListener!!.onUserAuthCheck(true, it)
            } ?: run {
                loginEventListener!!.onUserAuthCheck(false, null)
            }

        })
    }

    fun logout() {
        /** user log out from session  */
        firebase.signOut()
        if (logOutEventListener != null) {
            logOutEventListener!!.logOut()
        }
    }

    /** Just creates a new user, does not logs him in  */
    fun createNewUser(userData: UserData, password: String) {
        /** Create new user  */
        firebase.createUserWithEmailAndPassword(
            userData.emailId!!,
            password
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                val authData = it.result.user!!
                BookShareApplication.instance!!.uid = authData.uid
                /** Put data in firebase  */
                val userDataMap: MutableMap<String, String> = HashMap()
                userDataMap[Constants.NAME] = userData.userName!!
                FirebaseDatabase.getInstance().reference.child(Constants.USERs).child(authData.uid)
                    .setValue(userDataMap)
                /** User data map  */
                val phoneNumberDataMap: MutableMap<String, String> = HashMap()

                /** Correct Email  */
                var email = userData.emailId
                email = email!!.replace("[.]".toRegex(), "_dot_")
                email = email.replace("@".toRegex(), "_at_the_rate_")
                phoneNumberDataMap[email] = password
                phoneNumberDataMap[Constants.NAME] = userData.userName!!
                FirebaseDatabase.getInstance().reference.child(Constants.RPNs).child(email)
                    .setValue(phoneNumberDataMap)
                if (loginEventListener != null) {
                    loginEventListener!!.onUserCreated(true, null)
                }
            } else {
                if (loginEventListener != null) {
                    loginEventListener!!.onUserCreated(false, it.exception?.localizedMessage)
                }
            }

        }
    }

    /** Logs in user, also updates user's information to firebase database  */
    fun firstLogIn(userData: UserData, password: String) {
        /** Log in user  */
        firebase.signInWithEmailAndPassword(userData.emailId!!, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val authData = it.result.user!!
                BookShareApplication.instance!!.uid = authData.uid

                /** Correct Email  */
                var email = userData.emailId!!
                email = email.replace("[.]".toRegex(), "_dot_")
                email = email.replace("@".toRegex(), "_at_the_rate_")
               FirebaseDatabase.getInstance().reference.child(Constants.RPNs).child(email)
                    .child(email).setValue(password)
                if (loginEventListener != null) {
                    loginEventListener!!.onFirstTimeUserLogIn(true, null)
                }
            } else {
                if (loginEventListener != null) {
                    loginEventListener!!.onFirstTimeUserLogIn(false, null)
                }
            }
        }
    }

    /** Logs in for an existing user   */
    fun existingLogIn(email: String?, password: String?) {
        /** Log in user  */
        firebase.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener {
            if (it.isSuccessful) {
                if (loginEventListener != null) {
                    val authData = it.result.user!!
                    BookShareApplication.instance!!.uid = authData.uid
                    /** Correct Email  */
                    var email = authData.email?:""
                    email = email.replace("[.]".toRegex(), "_dot_")
                    email = email.replace("@".toRegex(), "_at_the_rate_")
                    FirebaseDatabase.getInstance().reference.child(Constants.RPNs).child(email)
                        .child(email).setValue(password)
                    loginEventListener!!.onExistingUserLogIn(true, authData.uid, null)
                } else {
                    loginEventListener!!.onExistingUserLogIn(
                        false,
                        null,
                        it.exception!!.localizedMessage
                    )
                }
            }
        }.addOnFailureListener {
            if (loginEventListener != null) {
                loginEventListener!!.onExistingUserLogIn(false, "", it.localizedMessage)
            }
        }
    }


    fun setLoginEvents(loginEventListener: LoginEvents?) {
        this.loginEventListener = loginEventListener
    }
    fun setLogoutEvents(loginEventListener: LogOutEvents?) {
        this.logOutEventListener = loginEventListener
    }

    /** Callback event from firebase logout*/
    interface LogOutEvents {
        fun logOut()
    }

    /** Callback events from firebase functions  */
    interface LoginEvents {
        fun onUserAuthCheck(isLoggedIn: Boolean, uid: String?)
        fun onUserCreated(isSuccessfullyCreated: Boolean, error: String?)
        fun onFirstTimeUserLogIn(isSuccessfullyLoggedIn: Boolean, error: String?)
        fun onExistingUser(isExisting: Boolean, email: String?, password: String?)
        fun onExistingUserLogIn(
            isSuccessfullyLoggedIn: Boolean,
            uid: String?,
            error: String?
        )

        fun onUserNameFetchedFromExistingUser(userName: String?)
        fun onUserUploadedBookIdFetchedFromExistingUser(bId: String?)
    }
}