package com.example.bookshare.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.example.bookshare.models.SearchedBook

/**
 * Describing book uploaded by a user.
 *
 */
class UploadedBook : Parcelable {
    /** id of book in database  */
    var id = 0

    /** `SearchedBook` model for basic book details  */
    val searchedBook: SearchedBook?

    /** Enum describing used book condition  */
    val condition: BookCondition?

    /** Used UploadedBook condition description by user  */
    val conditionDescription: String?

    /** UploadedBook upload date and time  */
    val uploadTimestamp: String?

    /** UploadedBook image  */
    var bookImage: ByteArray?

    /** private constructor  */
    private constructor(uploadedBookBuilder: UploadedBookBuilder) {
        searchedBook = uploadedBookBuilder.searchedBook
        condition = uploadedBookBuilder.condition
        conditionDescription = uploadedBookBuilder.conditionDescription
        uploadTimestamp = uploadedBookBuilder.uploadTimestamp
        bookImage = uploadedBookBuilder.bookImage
    }

    /** Builder Class  */
    class UploadedBookBuilder {
         var searchedBook: SearchedBook? = null
         var condition: BookCondition? = null
         var conditionDescription: String? = null
         var uploadTimestamp: String? = null
        var bookImage: ByteArray? = null
        fun addSearchedBook(searchedBook: SearchedBook?): UploadedBookBuilder {
            this.searchedBook = searchedBook
            return this
        }

        fun addCondition(condition: BookCondition?): UploadedBookBuilder {
            this.condition = condition
            return this
        }

        fun addConditionDescription(conditionDescription: String?): UploadedBookBuilder {
            this.conditionDescription = conditionDescription
            return this
        }

        fun addUploadTimestamp(uploadTimestamp: String?): UploadedBookBuilder {
            this.uploadTimestamp = uploadTimestamp
            return this
        }

        fun addBookImage(bookImage: ByteArray?): UploadedBookBuilder {
            this.bookImage = bookImage
            return this
        }

        fun build(): UploadedBook {
            return UploadedBook(this)
        }
    }
    /** */
    /****** Below methods make this class Parcelable  */
    /** */
    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        searchedBook = `in`.readValue(SearchedBook::class.java.classLoader) as SearchedBook?
        condition = `in`.readValue(BookCondition::class.java.classLoader) as BookCondition?
        conditionDescription = `in`.readString()
        uploadTimestamp = `in`.readString()
        bookImage = ByteArray(`in`.readInt())
        `in`.readByteArray(bookImage!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeValue(searchedBook)
        dest.writeValue(condition)
        dest.writeString(conditionDescription)
        dest.writeString(uploadTimestamp)
        dest.writeInt(bookImage!!.size)
        dest.writeByteArray(bookImage)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<UploadedBook> = object : Creator<UploadedBook> {
            override fun createFromParcel(`in`: Parcel): UploadedBook? {
                return UploadedBook(`in`)
            }

            override fun newArray(size: Int): Array<UploadedBook?> {
                return arrayOfNulls(size)
            }
        }
    }
}