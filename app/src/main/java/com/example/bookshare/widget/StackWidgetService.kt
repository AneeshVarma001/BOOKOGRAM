package com.example.bookshare.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.bookshare.R
import com.example.bookshare.contentprovider.UploadedBookProvider
import com.example.bookshare.db.UploadedBookTable
import com.example.bookshare.models.SearchedBook.SearchedBookBuilder
import com.example.bookshare.models.UploadedBook
import com.example.bookshare.models.UploadedBook.UploadedBookBuilder
import com.example.bookshare.util.BookUtil
import java.util.*

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

internal class StackRemoteViewsFactory(private val mContext: Context, intent: Intent) :
    RemoteViewsFactory {
    private var mCount = 0
    private val mWidgetItems: MutableList<UploadedBook> = ArrayList()
    private val mAppWidgetId: Int
    private var cursor: Cursor?

    init {
        mAppWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        cursor = mContext.contentResolver.query(
            UploadedBookProvider.CONTENT_URI,
            null,
            null,
            null,
            UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + " DESC"
        )
        updateCount()
    }

    override fun onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        fetchDataAndUpdateList()
    }

    override fun onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mWidgetItems.clear()
    }

    override fun getCount(): Int {
        return mCount
    }

    override fun getViewAt(position: Int): RemoteViews {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        if (position < mWidgetItems.size && mWidgetItems.size > 0) {
            val image = BookUtil.convertByteArrayToBitmap(mWidgetItems[position].bookImage)
            if (image != null) {
                rv.setImageViewBitmap(R.id.uploaded_book_book_image_view, image)
            }
            rv.setTextViewText(
                R.id.uploaded_book_book_title,
                mWidgetItems[position].searchedBook!!.title
            )
            rv.setTextViewText(
                R.id.uploaded_book_book_authors,
                TextUtils.join(",", mWidgetItems[position].searchedBook!!.authors!!)
            )
            rv.setTextViewText(
                R.id.uploaded_book_book_isbn,
                mWidgetItems[position].searchedBook!!.industryIdentifier
            )
            rv.setTextViewText(
                R.id.book_condition_, BookUtil.getStringForBookConditionEnum(
                    mWidgetItems[position].condition!!
                )
            )
            rv.setTextViewText(R.id.upload_timestamp_, mWidgetItems[position].uploadTimestamp)


            // Next, we set a fill-intent which will be used to fill-in the pending intent template
            // which is set on the collection view in StackWidgetProvider.
            val extras = Bundle()
            extras.putInt(StackWidgetProvider.EXTRA_ITEM, position)
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            rv.setOnClickFillInIntent(R.id.main_linear_layout, fillInIntent)

            // Return the remote views object.
        }
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
        if (cursor != null) {
            cursor!!.close()
        }
        cursor = mContext.contentResolver.query(
            UploadedBookProvider.CONTENT_URI,
            null,
            null,
            null,
            UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + " DESC"
        )
        updateCount()
        fetchDataAndUpdateList()
    }

    private fun updateCount() {
        mCount = if (cursor == null || cursor!!.count < 1) {
            0
        } else {
            cursor!!.count
        }
    }

    private fun fetchDataAndUpdateList() {
        if (mCount == 0) {
            return
        }
        /** If data previously available, clear it  */
        mWidgetItems.clear()
        while (cursor!!.moveToNext()) {
            /** Prepare uploaded book  */
            val uploadedBook = UploadedBookBuilder()
                .addCondition(
                    BookUtil.getBookConditionEnumFromString(
                        cursor!!.getString(cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION))
                    )
                )
                .addBookImage(cursor!!.getBlob(cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_IMAGE)))
                .addConditionDescription(
                    cursor!!.getString(
                        cursor!!.getColumnIndex(
                            UploadedBookTable.KEY_BOOK_CONDITION_DETAIL
                        )
                    )
                )
                .addUploadTimestamp(cursor!!.getString(cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP)))
                .addSearchedBook(
                    SearchedBookBuilder()
                        .addTitle(cursor!!.getString(cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_TITLE)))
                        .addAuthors(
                            ArrayList(
                                Arrays.asList(*cursor!!.getString(
                                    cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_AUTHORS)
                                )
                                    .split(" , ".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                                )
                            )
                        )
                        .addSubtitle(cursor!!.getString(cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_SUBTITLE)))
                        .addPublisher(cursor!!.getString(cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_PUBLISHERS)))
                        .addPublishedDate(
                            cursor!!.getString(
                                cursor!!.getColumnIndex(
                                    UploadedBookTable.KEY_BOOK_PUBLISHED_DATE
                                )
                            )
                        )
                        .addDescription(cursor!!.getString(cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_DESCRIPTION)))
                        .addIndustryIdentifier(
                            cursor!!.getString(
                                cursor!!.getColumnIndex(UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER)
                            )
                        )
                        .addThumbnailLink(
                            cursor!!.getString(
                                cursor!!.getColumnIndex(
                                    UploadedBookTable.KEY_BOOK_THUMBNAIL
                                )
                            )
                        )
                        .build()
                ).build()
            /** Add to list  */
            mWidgetItems.add(uploadedBook)
        }
        cursor!!.close()
    }
}