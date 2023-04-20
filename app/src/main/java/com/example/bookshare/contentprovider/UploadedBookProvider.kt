package com.example.bookshare.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.example.bookshare.db.SQLiteDb
import com.example.bookshare.db.UploadedBookTable
import java.util.*

class UploadedBookProvider : ContentProvider() {
    private var sqLiteDb: SQLiteDb? = null
    override fun onCreate(): Boolean {
        sqLiteDb = SQLiteDb(context)
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        // Using SQLiteQueryBuilder instead of query() method
        val queryBuilder = SQLiteQueryBuilder()

        // check if the caller has requested a column which does not exists
        checkColumns(projection)

        // Set the table
        queryBuilder.tables = UploadedBookTable.TABLE_NAME
        val uriType = sURIMatcher.match(uri)
        when (uriType) {
            UPLOADED_BOOKS -> {}
            UPLOADED_BOOKS_ID ->                 // adding the ID to the original query
                queryBuilder.appendWhere(
                    UploadedBookTable.KEY_ID + "="
                            + uri.lastPathSegment
                )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        val db = sqLiteDb!!.writableDatabase
        val cursor = queryBuilder.query(
            db, projection, selection,
            selectionArgs, null, null, sortOrder
        )
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = sqLiteDb!!.writableDatabase
        var id: Long = 0
        id = when (uriType) {
            UPLOADED_BOOKS -> sqlDB.insert(
                UploadedBookTable.TABLE_NAME,
                null,
                values
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse(BASE_PATH + "/" + id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = sqLiteDb!!.writableDatabase
        var rowsDeleted = 0
        rowsDeleted = when (uriType) {
            UPLOADED_BOOKS -> sqlDB.delete(
                UploadedBookTable.TABLE_NAME, selection,
                selectionArgs
            )
            UPLOADED_BOOKS_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.delete(
                        UploadedBookTable.TABLE_NAME,
                        UploadedBookTable.KEY_ID + "=" + id,
                        null
                    )
                } else {
                    sqlDB.delete(
                        UploadedBookTable.TABLE_NAME,
                        UploadedBookTable.KEY_ID + "=" + id
                                + " and " + selection,
                        selectionArgs
                    )
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = sqLiteDb!!.writableDatabase
        var rowsUpdated = 0
        rowsUpdated = when (uriType) {
            UPLOADED_BOOKS -> sqlDB.update(
                UploadedBookTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )
            UPLOADED_BOOKS_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.update(
                        UploadedBookTable.TABLE_NAME,
                        values,
                        UploadedBookTable.KEY_ID + "=" + id,
                        null
                    )
                } else {
                    sqlDB.update(
                        UploadedBookTable.TABLE_NAME,
                        values,
                        UploadedBookTable.KEY_ID + "=" + id
                                + " and "
                                + selection,
                        selectionArgs
                    )
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    private fun checkColumns(projection: Array<String>?) {
        val available = arrayOf(
            UploadedBookTable.KEY_ID,
            UploadedBookTable.KEY_BOOK_CONDITION,
            UploadedBookTable.KEY_BOOK_CONDITION_DETAIL,
            UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP,
            UploadedBookTable.KEY_BOOK_TITLE,
            UploadedBookTable.KEY_BOOK_AUTHORS,
            UploadedBookTable.KEY_BOOK_SUBTITLE,
            UploadedBookTable.KEY_BOOK_PUBLISHERS,
            UploadedBookTable.KEY_BOOK_PUBLISHED_DATE,
            UploadedBookTable.KEY_BOOK_DESCRIPTION,
            UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER,
            UploadedBookTable.KEY_BOOK_THUMBNAIL,
            UploadedBookTable.KEY_BOOK_IMAGE
        )
        if (projection != null) {
            val requestedColumns = HashSet(Arrays.asList(*projection))
            val availableColumns = HashSet(Arrays.asList(*available))
            // check if all columns which are requested are available
            require(availableColumns.containsAll(requestedColumns)) { "Unknown columns in projection" }
        }
    }

    companion object {
        private const val UPLOADED_BOOKS = 10
        private const val UPLOADED_BOOKS_ID = 20
        private const val AUTHORITY = "com.example.bookshare.contentprovider"
        private const val BASE_PATH = "uploaded_books"
        @JvmField
        val CONTENT_URI = Uri.parse(
            "content://" + AUTHORITY
                    + "/" + BASE_PATH
        )
        private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sURIMatcher.addURI(AUTHORITY, BASE_PATH, UPLOADED_BOOKS)
            sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", UPLOADED_BOOKS_ID)
        }
    }
}