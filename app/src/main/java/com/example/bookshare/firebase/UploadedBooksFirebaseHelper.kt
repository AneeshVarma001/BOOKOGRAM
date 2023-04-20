package com.example.bookshare.firebase

import android.text.TextUtils
import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.models.SearchedBook.SearchedBookBuilder
import com.example.bookshare.models.UploadedBook
import com.example.bookshare.models.UploadedBook.UploadedBookBuilder
import com.example.bookshare.util.BookUtil
import com.example.bookshare.util.Constants
import com.example.bookshare.util.FLog
import com.google.firebase.database.*
import java.util.*

class UploadedBooksFirebaseHelper {
    var uploadedBookFirebaseEvents: UploadedBookFirebaseEvents? = null

    /**
     * This method updates and insert data in the firebase database model
     * Given a uploaded book domain object, it saves it in json format in database.
     *
     * @param uploadedBook [UploadedBook] domain object.
     */
    fun uploadBook(uploadedBook: UploadedBook) {
        val firebase = FirebaseDatabase.getInstance().getReference(Constants.BOOKs)
        firebase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val title = uploadedBook.searchedBook!!.title
                val subTitle = uploadedBook.searchedBook.subTitle
                val authors = TextUtils.join(",", uploadedBook!!.searchedBook!!.authors!!)
                val publishers = uploadedBook.searchedBook.publisher
                val publishingDate = uploadedBook.searchedBook.publishedDate
                val description = uploadedBook.searchedBook.description
                val industryIdentifier = uploadedBook.searchedBook.industryIdentifier
                val thumbnailLink = uploadedBook.searchedBook.thumbnailLink
                val condition = BookUtil.getStringForBookConditionEnum(uploadedBook.condition!!)
                val conditionDescription = uploadedBook.conditionDescription
                val uploadedTimeStamp = uploadedBook.uploadTimestamp
                val map: HashMap<String, String> = object : HashMap<String, String>() {
                    init {
                        put("subtitle", subTitle!!)
                        put("authors", authors)
                        put("publisher", publishers!!)
                        put("publishingDate", publishingDate!!)
                        put("description", description!!)
                        put("industryIdentifier", industryIdentifier!!)
                        put("thumbnailLink", thumbnailLink!!)
                        put("condition", condition!!)
                        put("conditionDescription", conditionDescription!!)
                        put("uploadedTimestamp", uploadedTimeStamp!!)
                    }
                }
                firebase.child(BookShareApplication.instance!!.uid!!).child(title!!)
                    .setValue(map)
            }

            override fun onCancelled(firebaseError: DatabaseError) {
                FLog.d(this, firebaseError.toString())
            }
        })
    }

    /**
     * Deletes data in firebase real time database,
     * Firebase guarantees deletion even when user is offline,
     * it maintains an offline copy and as soon as user appears online,
     * it makes data consistent with online database.
     *
     * @param uploadedBook [UploadedBook] domain object model
     */
    fun deleteBook(uploadedBook: UploadedBook) {
        val firebase = FirebaseDatabase.getInstance().getReference(Constants.BOOKs)
        firebase.child(BookShareApplication.instance!!.uid!!)
            .child(uploadedBook.searchedBook!!.title!!).setValue(null)
    }

    /**
     * This method fetches all uploaded books present in firebase database.
     * <br></br>
     * This is called once in the beginning, when app is newly installed
     * and user is already registered.
     * Since, user is identified by phone number. So, if already registered
     * phone number is found, all her uploaded books are in sync with
     * online database.
     * <br></br>
     */
    fun fetchAllUploadedBooks() {
        val firebase = FirebaseDatabase.getInstance()
            .getReference(Constants.BOOKs + "/" + BookShareApplication.instance!!.uid!!)
        firebase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val uploadedBooklist = ArrayList<UploadedBook>()
                val map = dataSnapshot.value as HashMap<String, *>?
                for ((bookTitle, value) in map!!) {
                    val bookDataMap = value as HashMap<String, String>
                    val uploadedBook = UploadedBookBuilder()
                        .addCondition(BookUtil.getBookConditionEnumFromString(bookDataMap["condition"]!!))
                        .addBookImage(null)
                        .addConditionDescription(bookDataMap["conditionDescription"])
                        .addUploadTimestamp("uploadedTimestamp")
                        .addSearchedBook(
                            SearchedBookBuilder()
                                .addTitle(bookTitle)
                                .addAuthors(
                                    ArrayList(Arrays.asList(*bookDataMap["authors"]!!
                                        .split(" , ".toRegex()).dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                    )
                                    )
                                )
                                .addSubtitle(bookDataMap["subtitle"])
                                .addPublisher(bookDataMap["publisher"])
                                .addPublishedDate(bookDataMap["publishingDate"])
                                .addDescription(bookDataMap["description"])
                                .addIndustryIdentifier(bookDataMap["industryIdentifier"])
                                .addThumbnailLink(bookDataMap["thumbnailLink"])
                                .build()
                        ).build()
                    uploadedBooklist.add(uploadedBook)
                }
                if (uploadedBookFirebaseEvents != null) {
                    uploadedBookFirebaseEvents!!.onFetchAllUploadedBooks(uploadedBooklist)
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {}
        })
    }

    interface UploadedBookFirebaseEvents {
        fun onFetchAllUploadedBooks(uploadedBookArrayList: ArrayList<UploadedBook>?)
    }
}