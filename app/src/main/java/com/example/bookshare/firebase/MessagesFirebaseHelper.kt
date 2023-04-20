package com.example.bookshare.firebase

import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.models.Message
import com.example.bookshare.models.Message.MessageBuilder
import com.example.bookshare.util.Constants
import com.example.bookshare.util.MessageUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessagesFirebaseHelper {
   private var messageFirebaseEvents: MessageFirebaseEvents? = null

    fun setMessageFirebaseEvents(messageFirebaseEvents: MessageFirebaseEvents) {
        this.messageFirebaseEvents = messageFirebaseEvents
    }
    /**
     * Fetches all the messages send to or came from a given phone number
     * present in the user directory. This phone number has to be of a
     * friend in this user's directory, else, security exception is
     * thrown by firebase.
     *
     * @param phoneNumber of the friend.
     */
    fun getAllMessages(phoneNumber: String) {
        var email = phoneNumber
        email = email!!.replace("[.]".toRegex(), "_dot_")
        email = email.replace("@".toRegex(), "_at_the_rate_")
        var emailMy = BookShareApplication.instance!!.getEmail()
        emailMy = emailMy!!.replace("[.]".toRegex(), "_dot_")
        emailMy = emailMy.replace("@".toRegex(), "_at_the_rate_")

        val firebase = FirebaseDatabase.getInstance()
            .getReference(Constants.MESSAGES + "/" +  emailMy + "/" + email)
        firebase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messageArrayList = ArrayList<Message?>()
                val numberMessageDataMap = dataSnapshot.value as HashMap<String?, *>?
                if (numberMessageDataMap == null
                    || numberMessageDataMap.size == 0
                ) {
                    if (messageFirebaseEvents != null) {
                        messageFirebaseEvents!!.onAllMessagesFetched(messageArrayList, null)
                    }
                    return
                }
                for ((key, value) in numberMessageDataMap) {
                    if (key != null && value != null && key != "Message Read") {
                        val messageDataMap = value as HashMap<String, String>
                        val message = MessageBuilder()
                            .addMessage(messageDataMap["Message Body"])
                            .addTimeForMessage(key.toString())
                            .addPhoneNumber(messageDataMap["From"].toString())
                            .addMessageRead(true)
                            .build()
                        messageArrayList.add(message)
                    }
                }
                MessageUtil.sortMessages(messageArrayList)
                if (messageFirebaseEvents != null) {
                    messageFirebaseEvents!!.onAllMessagesFetched(messageArrayList, null)
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {
                if (messageFirebaseEvents != null) {
                    messageFirebaseEvents!!.onAllMessagesFetched(null, firebaseError)
                }
            }
        })
    }

    fun markAllMessagesAsRead(phoneNumber: String) {
        var email = phoneNumber
        email = email!!.replace("[.]".toRegex(), "_dot_")
        email = email.replace("@".toRegex(), "_at_the_rate_")

        val firebase = FirebaseDatabase.getInstance()
            .getReference(Constants.MESSAGES + "/" +  /*BookShareApplication.getInstance().getPhoneNumber() + "/" +*/email)
        firebase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val numberMessageDataMap = dataSnapshot.value as HashMap<String?, *>?
                if (numberMessageDataMap == null
                    || numberMessageDataMap.size == 0
                ) {
                    return
                }
                for ((key, value) in numberMessageDataMap) {
                    if (key != null && value != null) {
                        /** Set message read true  */
                        firebase.child(key.toString()).child("Message Read").setValue("true")
                    }
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {}
        })
    }

    fun sendMessage(phoneNumber: String?, message: String?) {
        var email = phoneNumber
        email = email!!.replace("[.]".toRegex(), "_dot_")
        email = email.replace("@".toRegex(), "_at_the_rate_")
        var emailMy = BookShareApplication.instance!!.getEmail()
        emailMy = emailMy!!.replace("[.]".toRegex(), "_dot_")
        emailMy = emailMy.replace("@".toRegex(), "_at_the_rate_")

        val firebase = FirebaseDatabase.getInstance().getReference(Constants.MESSAGES)
        val messageDataMap =  HashMap<String,String>()
        messageDataMap["From"] = emailMy
        messageDataMap["Message Body"] = message!!
        messageDataMap["Message Read"] = "true"

        firebase.child(emailMy).child(email).child((System.currentTimeMillis()).toString()).setValue(messageDataMap)

        messageDataMap["Message Read"] = "false"
        firebase.child(email).child(emailMy).child((System.currentTimeMillis()).toString()).setValue(messageDataMap)
    }/* + BookShareApplication.getInstance().getPhoneNumber()*/



    /**
     * Fetches all unread message counts.
     */
    val allUnreadMessageCount: Unit
        get() {
            val firebase = FirebaseDatabase.getInstance()
                .getReference(Constants.MESSAGES + "/" /* + BookShareApplication.getInstance().getPhoneNumber()*/)
            firebase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val numberUnreadMessageMap = HashMap<String, Int>()
                    var totalCount = 0
                    val numberTimeMap = dataSnapshot.value as HashMap<String, *>?
                    if (numberTimeMap == null || numberTimeMap.size == 0) {
                        if (messageFirebaseEvents != null) {
                            messageFirebaseEvents!!.onAllUnReadMessageCountFetched(
                                HashMap(),
                                0,
                                null
                            )
                        }
                        return
                    }
                    for ((key, value) in numberTimeMap) {
                        var counter = 0
                        val timeMessageMap = value as HashMap<String, *>
                        for ((_, value1) in timeMessageMap) {
                            val messageMap = value1 as HashMap<String, String>
                            if (messageMap["Message Read"].toString() == "false") {
                                counter++
                                totalCount++
                            }
                        }
                        numberUnreadMessageMap[key] = counter
                    }
                    if (messageFirebaseEvents != null) {
                        messageFirebaseEvents!!.onAllUnReadMessageCountFetched(
                            numberUnreadMessageMap,
                            totalCount,
                            null
                        )
                    }
                }

                override fun onCancelled(firebaseError: DatabaseError) {
                    if (messageFirebaseEvents != null) {
                        messageFirebaseEvents!!.onAllUnReadMessageCountFetched(
                            null,
                            0,
                            firebaseError
                        )
                    }
                }
            })
        }

    interface MessageFirebaseEvents {
        fun onAllMessagesFetched(
            messageArrayList: ArrayList<Message?>?,
            firebaseError: DatabaseError?
        )

        fun onAllUnReadMessageCountFetched(
            numberUnreadMessageMap: HashMap<String, Int>?,
            totalCount: Int,
            firebaseError: DatabaseError?
        )
    }
}