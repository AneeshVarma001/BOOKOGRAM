package com.example.bookshare.firebase

import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.util.Constants
import com.example.bookshare.util.FLog.d
import com.google.firebase.database.*

class RPNFirebaseHelper {
    private var rpnEvents: RPNEvents? = null
    var firebase: DatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.RPNs)

    init {
        //        firebase = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + Constants.RPNs);
    }

    /** Checks RPN list  */
    fun fetchListOfRPN(): List<HashMap<String, String>>? {
        /** Checks RPN list  */
        firebase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (rpnEvents == null) {
                    return
                }
                var myEmail = BookShareApplication.instance?.getEmail()
                myEmail = myEmail?.replace("[.]".toRegex(), "_dot_")
                myEmail = myEmail?.replace("@".toRegex(), "_at_the_rate_")
                val returnList: MutableList<HashMap<String, String>> = ArrayList()
                val mapHashMap = dataSnapshot.value as HashMap<String, *>?
                for ((key, value) in mapHashMap!!) {

                    if (myEmail != key) {
                        d(this, key)
                        val data = HashMap<String, String>()
                        data["email"] = key
                        val childHashMap = value as HashMap<String, String>
                        data["name"] = childHashMap[Constants.NAME] as String
                        returnList.add(data)
                    }
                }
                rpnEvents!!.onRPNFetched(returnList, null)
            }

            override fun onCancelled(firebaseError: DatabaseError) {
                if (rpnEvents != null) {
                    rpnEvents!!.onRPNFetched(null, firebaseError)
                }
            }
        })
        return null
    }

    fun setRpnEvents(rpnEvents: RPNEvents?) {
        this.rpnEvents = rpnEvents
    }

    interface RPNEvents {
        fun onRPNFetched(rpnList: List<HashMap<String, String>>?, firebaseError: DatabaseError?)
    }
}