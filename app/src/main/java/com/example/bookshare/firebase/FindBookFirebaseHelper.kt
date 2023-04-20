package com.example.bookshare.firebase

import com.example.bookshare.models.FindBookMetaData
import com.example.bookshare.models.FindBookMetaData.FindBookMetaDataBuilder
import com.example.bookshare.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FindBookFirebaseHelper {
    private var findBookFirebaseEvents: FindBookFirebaseEvents? = null
    private var findBookFirebaseCompletionEvents: FindBookFirebaseCompletionEvents? = null
    fun findBook(searchBookTitle: String, findBookMetaData: FindBookMetaData) {
        val bId = findBookMetaData.bId
        val firebase = FirebaseDatabase.getInstance().getReference(Constants.BOOKs + "/" + bId)
        firebase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as HashMap<String?, *>?
                if (map == null
                    || map.size == 0
                ) {
                    if (findBookFirebaseCompletionEvents != null) {
                        findBookFirebaseCompletionEvents!!.onSearchComplete(false, null)
                    }
                    return
                }
                for ((bookTitle) in map) {
                    if (bookTitle == null) {
                        continue
                    }
                    if (bookTitle == searchBookTitle) {
                        if (findBookFirebaseCompletionEvents != null) {
                            findBookFirebaseCompletionEvents!!.onSearchComplete(
                                true,
                                findBookMetaData
                            )
                            return
                        }
                    }
                }
                if (findBookFirebaseCompletionEvents != null) {
                    findBookFirebaseCompletionEvents!!.onSearchComplete(false, null)
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {}
        })
    }

    fun completeFindBookMetaDataList(findBookMetaDataArrayList: ArrayList<FindBookMetaData>) {
        val firebase = FirebaseDatabase.getInstance().getReference(Constants.USERs)
        firebase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val numberbIdMap = HashMap<String?, String>()
                val map = dataSnapshot.value as HashMap<String, *>?
                if (map == null
                    || map.size == 0
                ) {
                    if (findBookFirebaseEvents != null) {
                        findBookFirebaseEvents!!.onAllFindBookMetaDataFetched(ArrayList())
                    }
                    return
                }
                for ((bId, value) in map) {
                    val uDatamap = value as HashMap<String, String>
                    numberbIdMap[uDatamap[Constants.NAME].toString()] = bId
                }
                val returningFindBookMetaDataArrayList = ArrayList<FindBookMetaData?>()
                /** Looping through each find book meta data  */
                for (i in findBookMetaDataArrayList.indices) {
                    val bId = numberbIdMap[findBookMetaDataArrayList[i].name]
                    if (bId != null) {
                        val findBookMetaData = FindBookMetaDataBuilder()
                            .addName(findBookMetaDataArrayList[i].name)
                            .addBId(bId)
                            .addPhoneNumber(findBookMetaDataArrayList[i].phoneNumber)
                            .build()
                        returningFindBookMetaDataArrayList.add(findBookMetaData)
                    }
                }
                if (findBookFirebaseEvents != null) {
                    findBookFirebaseEvents!!.onAllFindBookMetaDataFetched(
                        returningFindBookMetaDataArrayList
                    )
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {}
        })
    }

    fun setFindBookFirebaseEvents(findBookFirebaseEvents: FindBookFirebaseEvents?) {
        this.findBookFirebaseEvents = findBookFirebaseEvents
    }

    fun setFindBookFirebaseCompletionEvents(findBookFirebaseCompletionEvents: FindBookFirebaseCompletionEvents?) {
        this.findBookFirebaseCompletionEvents = findBookFirebaseCompletionEvents
    }

    interface FindBookFirebaseEvents {
        fun onAllFindBookMetaDataFetched(findBookMetaDataArrayList: ArrayList<FindBookMetaData?>?)
    }

    interface FindBookFirebaseCompletionEvents {
        fun onSearchComplete(isSuccessful: Boolean, findBookMetaData: FindBookMetaData?)
    }
}