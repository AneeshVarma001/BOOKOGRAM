package com.example.bookshare.db

import android.content.ContentValues
import android.text.TextUtils
import com.example.bookshare.models.UploadedBook
import com.example.bookshare.util.BookUtil.getStringForBookConditionEnum

object UploadedBookTable {
    const val TABLE_NAME = "uploadedBookTable"
    const val KEY_ID = "id"
    const val KEY_BOOK_CONDITION = "book_condition"
    const val KEY_BOOK_CONDITION_DETAIL = "book_condition_detail"
    const val KEY_BOOK_UPLOAD_TIMESTAMP = "book_upload_timestamp"
    const val KEY_BOOK_TITLE = "book_title"
    const val KEY_BOOK_AUTHORS = "book_authors"
    const val KEY_BOOK_SUBTITLE = "book_subtitle"
    const val KEY_BOOK_PUBLISHERS = "book_publishers"
    const val KEY_BOOK_PUBLISHED_DATE = "book_published_date"
    const val KEY_BOOK_DESCRIPTION = "book_description"
    const val KEY_BOOK_INDUSTRY_IDENTIFIER = "book_industry_identifier"
    const val KEY_BOOK_THUMBNAIL = "book_thumbnail"
    const val KEY_BOOK_IMAGE = "book_image"
    val createQuery: String
        get() = ("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_BOOK_CONDITION + " TEXT,"
                + KEY_BOOK_CONDITION_DETAIL + " TEXT,"
                + KEY_BOOK_UPLOAD_TIMESTAMP + " TEXT,"
                + KEY_BOOK_TITLE + " TEXT,"
                + KEY_BOOK_AUTHORS + " TEXT,"
                + KEY_BOOK_SUBTITLE + " TEXT,"
                + KEY_BOOK_PUBLISHERS + " TEXT,"
                + KEY_BOOK_PUBLISHED_DATE + " TEXT,"
                + KEY_BOOK_DESCRIPTION + " TEXT,"
                + KEY_BOOK_INDUSTRY_IDENTIFIER + " TEXT,"
                + KEY_BOOK_THUMBNAIL + " TEXT,"
                + KEY_BOOK_IMAGE + " BLOB"
                + ")")

    fun getContentValues(uploadedBook: UploadedBook): ContentValues {
        val values = ContentValues()
        values.put(
            KEY_BOOK_CONDITION, getStringForBookConditionEnum(
                uploadedBook.condition!!
            )
        )
        values.put(KEY_BOOK_CONDITION_DETAIL, uploadedBook.conditionDescription)
        values.put(KEY_BOOK_UPLOAD_TIMESTAMP, uploadedBook.uploadTimestamp)
        values.put(KEY_BOOK_TITLE, uploadedBook.searchedBook!!.title)
        values.put(KEY_BOOK_AUTHORS, TextUtils.join(",", uploadedBook.searchedBook.authors!!))
        values.put(KEY_BOOK_SUBTITLE, uploadedBook.searchedBook.subTitle)
        values.put(KEY_BOOK_PUBLISHERS, uploadedBook.searchedBook.publisher)
        values.put(KEY_BOOK_PUBLISHED_DATE, uploadedBook.searchedBook.publishedDate)
        values.put(KEY_BOOK_DESCRIPTION, uploadedBook.searchedBook.description)
        values.put(KEY_BOOK_INDUSTRY_IDENTIFIER, uploadedBook.searchedBook.industryIdentifier)
        values.put(KEY_BOOK_THUMBNAIL, uploadedBook.searchedBook.thumbnailLink)
        values.put(KEY_BOOK_IMAGE, uploadedBook.bookImage)
        return values
    }
}