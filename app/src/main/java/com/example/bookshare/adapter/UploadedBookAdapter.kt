package com.example.bookshare.adapter

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter
import com.example.bookshare.R
import com.example.bookshare.activities.BookConditionActivity
import com.example.bookshare.contentprovider.UploadedBookProvider
import com.example.bookshare.db.UploadedBookTable
import com.example.bookshare.firebase.UploadedBooksFirebaseHelper
import com.example.bookshare.models.SearchedBook.SearchedBookBuilder
import com.example.bookshare.models.UploadedBook
import com.example.bookshare.models.UploadedBook.UploadedBookBuilder
import com.example.bookshare.util.BookUtil.convertByteArrayToBitmap
import com.example.bookshare.util.BookUtil.getBookConditionEnumFromString
import com.example.bookshare.util.Constants
import com.example.bookshare.util.FLog.d
import com.example.bookshare.widget.StackWidgetProvider
import java.util.*

class UploadedBookAdapter(
    private val context: Context,
    private val cursor: Cursor,
    private val inflateLayoutId: Int
) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any {
        return Any()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /** Holder class for this adapter  */
    internal inner class UploadedBookAdapterHolder() {
        var bookImageView: ImageView? = null
        var bookTitleView: TextView? = null
        var bookAuthorsView: TextView? = null
        var bookIsbnView: TextView? = null
        var bookConditionView: TextView? = null
        var bookUploadTimestamp: TextView? = null
        var editDeleteLayout: LinearLayout? = null
        var editRelativeLayout: RelativeLayout? = null
        var deleteRelativeLayout: RelativeLayout? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        /** Get the convertView  */
        var view = convertView
        /** Holder instance  */
        val uploadedBookAdapterHolder: UploadedBookAdapterHolder
        /** Moving cursor  */
        cursor.moveToPosition(position)
        /** if convertView is null, its first inflation for list item view  */
        if (view == null) {
            view = layoutInflater.inflate(inflateLayoutId, parent, false)
            uploadedBookAdapterHolder = UploadedBookAdapterHolder()
            uploadedBookAdapterHolder.bookImageView =
                view.findViewById<View>(R.id.uploaded_book_book_image_view) as ImageView
            uploadedBookAdapterHolder.bookTitleView =
                view.findViewById<View>(R.id.uploaded_book_book_title) as TextView
            uploadedBookAdapterHolder.bookAuthorsView =
                view.findViewById<View>(R.id.uploaded_book_book_authors) as TextView
            uploadedBookAdapterHolder.bookIsbnView =
                view.findViewById<View>(R.id.uploaded_book_book_isbn) as TextView
            uploadedBookAdapterHolder.bookConditionView =
                view.findViewById<View>(R.id.book_condition_) as TextView
            uploadedBookAdapterHolder.bookUploadTimestamp =
                view.findViewById<View>(R.id.upload_timestamp_) as TextView
            uploadedBookAdapterHolder.editDeleteLayout =
                view.findViewById<View>(R.id.edit_linear_layout) as LinearLayout
            uploadedBookAdapterHolder.editRelativeLayout =
                view.findViewById<View>(R.id.modify_relative_layout) as RelativeLayout
            uploadedBookAdapterHolder.deleteRelativeLayout =
                view.findViewById<View>(R.id.delete_relative_layout) as RelativeLayout
            view.tag = uploadedBookAdapterHolder
        } else {
            /** else, get the holder from the view's tag  */
            uploadedBookAdapterHolder = view.tag as UploadedBookAdapterHolder
        }
        val image =
            convertByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_IMAGE)))
        if (image != null) {
            uploadedBookAdapterHolder.bookImageView!!.setImageBitmap(image)
        }
        uploadedBookAdapterHolder.bookTitleView!!.text =
            cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_TITLE))
        uploadedBookAdapterHolder.bookAuthorsView!!.text =
            cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_AUTHORS))
        uploadedBookAdapterHolder.bookIsbnView!!.text =
            cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER))
        uploadedBookAdapterHolder.bookConditionView!!.text =
            cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION))
        uploadedBookAdapterHolder.bookUploadTimestamp!!.text =
            cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP))
        val uploadedBook = UploadedBookBuilder()
            .addCondition(
                getBookConditionEnumFromString(
                    cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION))
                )
            )
            .addBookImage(cursor.getBlob(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_IMAGE)))
            .addConditionDescription(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION_DETAIL)))
            .addUploadTimestamp(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP)))
            .addSearchedBook(SearchedBookBuilder()
                .addTitle(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_TITLE)))
                .addAuthors(
                    ArrayList(
                        Arrays.asList(*(cursor.getString(
                            cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_AUTHORS)
                        )).split(" , ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()))
                )
                .addSubtitle(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_SUBTITLE)))
                .addPublisher(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_PUBLISHERS)))
                .addPublishedDate(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_PUBLISHED_DATE)))
                .addDescription(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_DESCRIPTION)))
                .addIndustryIdentifier(
                    cursor.getString(
                        cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER)
                    )
                )
                .addThumbnailLink(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_THUMBNAIL)))
                .build()
            ).build()
        uploadedBook.id = cursor.getInt(cursor.getColumnIndex(UploadedBookTable.KEY_ID))
        /** Set on click listener  */
        view!!.setOnClickListener(
            OnClickListenerForUploadedItem(
                uploadedBook,
                uploadedBookAdapterHolder
            )
        )
        /** return the inflated view  */
        return view
    }

    /**
     * On Item Click listener
     */
    internal inner class OnClickListenerForUploadedItem(
        val uploadedBook: UploadedBook,
        val uploadedBookAdapterHolder: UploadedBookAdapterHolder
    ) : View.OnClickListener {
        init {
            uploadedBookAdapterHolder.editRelativeLayout!!.setOnClickListener(View.OnClickListener {
                /** Modify  */
                /** Modify  */
                val intent = Intent(context, BookConditionActivity::class.java)
                intent.putExtra(Constants.UPDATE_REQUIRED, true)
                intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBook)
                intent.putExtra(Constants.BOOK_ID, uploadedBook.id)
                context.startActivity(intent)
                uploadedBookAdapterHolder.editDeleteLayout!!.visibility = View.GONE
            })
            uploadedBookAdapterHolder.deleteRelativeLayout!!.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(v: View) {
                    /** Delete  */
                    val rowsDeleted = context.contentResolver.delete(
                        UploadedBookProvider.CONTENT_URI,
                        UploadedBookTable.KEY_ID + " = " + uploadedBook.id,
                        null
                    )
                    d(this, rowsDeleted.toString())
                    /** Update online data  */
                    val uploadedBooksFirebaseHelper = UploadedBooksFirebaseHelper()
                    uploadedBooksFirebaseHelper.deleteBook(uploadedBook)
                    /** Update Widget  */
                    val intent = Intent(context, StackWidgetProvider::class.java)
                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    context.sendBroadcast(intent)
                    uploadedBookAdapterHolder.editDeleteLayout!!.visibility = View.GONE
                }
            })
        }

        override fun onClick(v: View) {
            if (uploadedBookAdapterHolder.editDeleteLayout!!.visibility == View.VISIBLE) {
                uploadedBookAdapterHolder.editDeleteLayout!!.visibility = View.GONE
            } else {
                uploadedBookAdapterHolder.editDeleteLayout!!.visibility = View.VISIBLE
            }
        }
    }
}