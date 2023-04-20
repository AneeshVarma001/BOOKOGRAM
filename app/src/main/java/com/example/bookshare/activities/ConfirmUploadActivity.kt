package com.example.bookshare.activities

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bookshare.R
import com.example.bookshare.contentprovider.UploadedBookProvider
import com.example.bookshare.databinding.ActivityConfirmUploadBinding
import com.example.bookshare.db.UploadedBookTable
import com.example.bookshare.firebase.UploadedBooksFirebaseHelper
import com.example.bookshare.models.BookCondition
import com.example.bookshare.models.UploadedBook
import com.example.bookshare.models.UploadedBook.UploadedBookBuilder
import com.example.bookshare.util.BookUtil
import com.example.bookshare.util.BundleUtil
import com.example.bookshare.util.Constants
import com.example.bookshare.util.FLog
import com.example.bookshare.widget.StackWidgetProvider
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * This activity confirms upload of book.

 */
class ConfirmUploadActivity : AppCompatActivity() {
    var binding: ActivityConfirmUploadBinding? = null

    /**
     * [UploadedBook] domain object.
     */
    var uploadedBook: UploadedBook? = null

    /**
     * Bitmap image of book
     */
    var bookImage: Bitmap? = null

    /**
     * boolean value denoting update required for uploaded book
     */
    var updateRequired = false

    /**
     * Book id for the book present in database, used to update book in database.
     */
    var bookId = 0

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmUploadBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        /**
         * Get the dynamic elements passed on with intent or from saved instance state.
         */
        uploadedBook = BundleUtil.getUploadedBookFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.UPLOADED_BOOK_KEY,
            null
        )
        bookImage = BookUtil.convertByteArrayToBitmap(uploadedBook!!.bookImage)
        updateRequired = BundleUtil.getBooleanFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.UPDATE_REQUIRED,
            false
        )
        bookId =
            BundleUtil.getIntFromBundle(savedInstanceState, intent.extras, Constants.BOOK_ID, -1)
        val searchedBook = uploadedBook!!.searchedBook
        /**
         * Set Searched Book Details
         */
        setTitle(searchedBook!!.title, searchedBook.subTitle)
        setAuthors(searchedBook.authors)
        setDescription(searchedBook.description)
        setPublishingDetails(searchedBook.publisher, searchedBook.publishedDate)
        val bookCondition = uploadedBook!!.condition
        setConditionalDetails(bookCondition)
        /**
         * Get date formatted
         */
        val df: DateFormat = SimpleDateFormat(getString(R.string.date_format))
        val date = df.format(Calendar.getInstance().time)
        binding!!.previewTimestampTextView.text = date
        /**
         * if book image is null, picasso was not able to load image, now load it.
         */
        if (bookImage == null && searchedBook.thumbnailLink!!.length != 0) {
            Glide.with(this).load(searchedBook.thumbnailLink).into(binding!!.bookPreviewImageView)
        } else {
            binding!!.bookPreviewImageView.setImageBitmap(bookImage)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        /**
         * Saving dynamic elements for this activity.
         */
        super.onSaveInstanceState(outState)
        outState.putParcelable(Constants.UPLOADED_BOOK_KEY, uploadedBook)
        outState.putBoolean(Constants.UPDATE_REQUIRED, updateRequired)
        outState.putInt(Constants.BOOK_ID, bookId)
    }

    /**
     * Back icon pressed by user
     *
     * @param v view for the back icon
     */
    fun backClicked(v: View?) {
        super.onBackPressed()
    }

    /**
     * Tick clicked by user.
     * This method prepares [UploadedBook] domain object.
     * Then, further if update is required, it updates using [UploadedBookProvider]
     * custom content provider into SQLite database, else inserts data.
     *
     * @param v view for the tick icon
     */
    fun tickClicked(v: View?) {
        /** Prepares [UploadedBook] domain object  */
        val uploadedBookData = UploadedBookBuilder()
            .addSearchedBook(uploadedBook!!.searchedBook)
            .addCondition(uploadedBook!!.condition)
            .addBookImage(BookUtil.convertBitmapToByteArray(bookImage)!!)
            .addConditionDescription(binding!!.additionalDetailsAppCompatEditText.text.toString())
            .addUploadTimestamp(binding!!.previewTimestampTextView.text.toString())
            .build()
        if (updateRequired) {
            /**
             * Update Data
             */
            val rowsAffected = contentResolver.update(
                UploadedBookProvider.CONTENT_URI,
                UploadedBookTable.getContentValues(uploadedBookData),
                UploadedBookTable.KEY_ID + " = " + bookId,
                null
            )
            FLog.d(this, rowsAffected.toString())
        } else {
            /**
             * Insert data
             */
            val result = contentResolver.insert(
                UploadedBookProvider.CONTENT_URI,
                UploadedBookTable.getContentValues(uploadedBookData)
            )
            FLog.d(this, uploadedBookData.toString())
            FLog.d(this, result.toString())
        }
        /**
         * Update in online firebase database
         */
        val uploadedBooksFirebaseHelper = UploadedBooksFirebaseHelper()
        uploadedBooksFirebaseHelper.uploadBook(uploadedBookData)
        /**
         * Update Widget
         */
        val intent = Intent(this, StackWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        sendBroadcast(intent)
        /**
         * close this activity and below activities
         */
        setResult(Constants.CLOSE_ACTIVITY)
        finish()
    }

    /**
     * Sets title for the book
     *
     * @param title of the book
     * @param subTitle of the book
     */
    fun setTitle(title: String?, subTitle: String?) {
        if (subTitle == null
            || subTitle.length == 0
        ) {
            binding!!.previewTitleTextView.text = title
        } else {
            binding!!.previewTitleTextView.text = String.format("%s:%s", title, subTitle)
        }
    }

    /**
     * Set authors for the book
     *
     * @param authors of the book
     */
    fun setAuthors(authors: ArrayList<String?>?) {
        if (authors == null
            || authors.size == 0
        ) {
            binding!!.previewAuthorTextView.text = ""
        } else {
            binding!!.previewAuthorTextView.text =
                getString(R.string.by) + TextUtils.join(",", authors)
        }
    }

    /**
     * Sets book description.
     *
     * @param description of the book.
     */
    fun setDescription(description: String?) {
        if (description == null
            || description.length == 0
        ) {
            binding!!.previewDescriptionTextView.setText(R.string.no_description_found)
        } else {
            binding!!.previewDescriptionTextView.text = description
        }
    }

    /**
     * Set publishing details for the book
     *
     * @param publisher of the book
     * @param publishingDate of the book.
     */
    fun setPublishingDetails(publisher: String?, publishingDate: String?) {
        if (publisher == null
            || publisher.length == 0
        ) {
            binding!!.previewPublishingDetailsTextView.text = ""
        } else {
            if (publishingDate == null
                || publishingDate.length == 0
            ) {
                binding!!.previewPublishingDetailsTextView.text = publisher
            } else {
                binding!!.previewPublishingDetailsTextView.text =
                    String.format("%s - %s", publisher, publishingDate)
            }
        }
    }

    /**
     * Given the [BookCondition] enum, this method sets book condition
     * details, which include : setting book condition name and setting book condition
     * detail.
     *
     * @param condition [BookCondition] enum
     */
    private fun setConditionalDetails(condition: BookCondition?) {
        if (condition == null) {
            return
        }
        when (condition) {
            BookCondition.POOR -> {
                binding!!.bookConditionTextView.setText(R.string.poor)
                binding!!.bookConditionDetailsTextView.setText(R.string.poor_description)
            }
            BookCondition.LOOSE_BINDING -> {
                binding!!.bookConditionTextView.setText(R.string.losse_binding)
                binding!!.bookConditionDetailsTextView.setText(R.string.loose_binding_description)
            }
            BookCondition.BINDING_COPY -> {
                binding!!.bookConditionTextView.setText(R.string.binding_copy)
                binding!!.bookConditionDetailsTextView.setText(R.string.binding_copy_description)
            }
            BookCondition.FAIR -> {
                binding!!.bookConditionTextView.setText(R.string.fair)
                binding!!.bookConditionDetailsTextView.setText(R.string.fair_description)
            }
            BookCondition.GOOD -> {
                binding!!.bookConditionTextView.setText(R.string.good)
                binding!!.bookConditionDetailsTextView.setText(R.string.good_description)
            }
            BookCondition.FINE -> {
                binding!!.bookConditionTextView.setText(R.string.fine)
                binding!!.bookConditionDetailsTextView.setText(R.string.fine_description)
            }
            BookCondition.NEW -> {
                binding!!.bookConditionTextView.setText(R.string.new_string)
                binding!!.bookConditionDetailsTextView.setText(R.string.new_description)
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
    }
}