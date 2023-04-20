package com.example.bookshare.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Describing book result from google book api.
 *
 */
class SearchedBook : Parcelable {
    /** Title of book  */
    val title: String?

    /** Sub-title of book, if exists  */
    val subTitle: String?

    /** List of book authors  */
    val authors: ArrayList<String?>?

    /** The publishing press name for this book  */
    val publisher: String?

    /** Date of publishing for this book  */
    val publishedDate: String?

    /** UploadedBook description  */
    val description: String?

    /** This is a 10-digit or 13-digit isbn value for the book  */
    val industryIdentifier: String?

    /** UploadedBook thumbnail link (normal size)  */
    val thumbnailLink: String?

    /** private constructor for this class  */
    private constructor(searchedBookBuilder: SearchedBookBuilder) {
        title = searchedBookBuilder.title
        subTitle = searchedBookBuilder.subTitle
        authors = searchedBookBuilder.authors
        publishedDate = searchedBookBuilder.publishedDate
        publisher = searchedBookBuilder.publisher
        description = searchedBookBuilder.description
        industryIdentifier = searchedBookBuilder.industryIdentifier
        thumbnailLink = searchedBookBuilder.thumbnailLink
    }

    /** Builder class  */
     class SearchedBookBuilder {
         var title: String? = null
         var subTitle: String? = null
         var authors: ArrayList<String?>? = null
         var publisher: String? = null
         var publishedDate: String? = null
         var description: String? = null
         var industryIdentifier: String? = null
         var thumbnailLink: String? = null
        fun addTitle(title: String?): SearchedBookBuilder {
            this.title = title
            return this
        }

        fun addSubtitle(subTitle: String?): SearchedBookBuilder {
            this.subTitle = subTitle
            return this
        }

        fun addDescription(description: String?): SearchedBookBuilder {
            this.description = description
            return this
        }

        fun addAuthors(authors: ArrayList<String?>?): SearchedBookBuilder {
            this.authors = authors
            return this
        }

        fun addPublisher(publisher: String?): SearchedBookBuilder {
            this.publisher = publisher
            return this
        }

        fun addPublishedDate(publishedDate: String?): SearchedBookBuilder {
            this.publishedDate = publishedDate
            return this
        }

        fun addIndustryIdentifier(industryIdentifier: String?): SearchedBookBuilder {
            this.industryIdentifier = industryIdentifier
            return this
        }

        fun addThumbnailLink(thumbnailLink: String?): SearchedBookBuilder {
            this.thumbnailLink = thumbnailLink
            return this
        }

        fun build(): SearchedBook {
            return SearchedBook(this)
        }
    }
    /** */
    /****** Below methods make this class Parcelable  */
    /** */
    protected constructor(`in`: Parcel) {
        title = `in`.readString()
        subTitle = `in`.readString()
        if (`in`.readByte().toInt() == 0x01) {
            authors = ArrayList()
            `in`.readList(authors, String::class.java.classLoader)
        } else {
            authors = null
        }
        publisher = `in`.readString()
        publishedDate = `in`.readString()
        description = `in`.readString()
        industryIdentifier = `in`.readString()
        thumbnailLink = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(subTitle)
        if (authors == null) {
            dest.writeByte(0x00.toByte())
        } else {
            dest.writeByte(0x01.toByte())
            dest.writeList(authors)
        }
        dest.writeString(publisher)
        dest.writeString(publishedDate)
        dest.writeString(description)
        dest.writeString(industryIdentifier)
        dest.writeString(thumbnailLink)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<SearchedBook> = object : Creator<SearchedBook> {
            override fun createFromParcel(`in`: Parcel): SearchedBook? {
                return SearchedBook(`in`)
            }

            override fun newArray(size: Int): Array<SearchedBook?> {
                return arrayOfNulls(size)
            }
        }
    }
}