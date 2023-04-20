package com.example.bookshare.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshare.R
import com.example.bookshare.databinding.ActivityBookConditionBinding
import com.example.bookshare.models.BookCondition
import com.example.bookshare.models.UploadedBook
import com.example.bookshare.models.UploadedBook.UploadedBookBuilder
import com.example.bookshare.util.BookUtil
import com.example.bookshare.util.BundleUtil
import com.example.bookshare.util.Constants

/**
 * Book Condition describing activity.
 */
class BookConditionActivity : AppCompatActivity() {
    /**
     * [UploadedBook] instance.
     */
    var uploadedBook: UploadedBook? = null

    /**
     * Bitmap image for book
     */
    var bookImage: Bitmap? = null

    /**
     * boolean value for update required state.
     */
    var updateRequired = false

    /**
     * The id of book as in SQLite database.
     * This is used to update record in SQLite.
     */
    var bookId = 0
    private var binding: ActivityBookConditionBinding? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookConditionBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        /**
         * Fetch dynamic states passed with intent or saved on orientation changes.
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
        /**
         * If we have condition already, set that up in seek bar
         */
        if (uploadedBook!!.condition != null) {
            val bookCondition = uploadedBook!!.condition
            binding!!.seekBarBookCondition.progress = getProgressFromBookCondition(bookCondition!!)
        }
        /**
         * Set book condition details based on book condition set in seek bar.
         */
        setConditionalDetails(binding!!.seekBarBookCondition.progress)
        /**
         * Listen to seek bar events
         */
        binding!!.seekBarBookCondition.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progressValue: Int,
                fromUser: Boolean
            ) {
                setConditionalDetails(progressValue)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        /**
         * Important to save some dynamic elements.
         */
        savedInstanceState.putParcelable(Constants.UPLOADED_BOOK_KEY, uploadedBook)
        savedInstanceState.putBoolean(Constants.UPDATE_REQUIRED, updateRequired)
        savedInstanceState.putInt(Constants.BOOK_ID, bookId)
    }

    /**
     * Tick button clicked by user.
     * This method prepares [UploadedBook] data object,
     * and passes this data to [ConfirmUploadActivity].
     *
     * @param v view for tick button.
     */
    fun tickClicked(v: View?) {
        val intent = Intent(this, ConfirmUploadActivity::class.java)
        val uploadedBookData = UploadedBookBuilder()
            .addSearchedBook(uploadedBook!!.searchedBook)
            .addCondition(getBookCondition(binding!!.seekBarBookCondition.progress))
            .addBookImage(BookUtil.convertBitmapToByteArray(bookImage)!!)
            .build()
        intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBookData)
        intent.putExtra(Constants.UPDATE_REQUIRED, updateRequired)
        intent.putExtra(Constants.BOOK_ID, bookId)
        startActivityForResult(intent, 7)
    }

    /**
     * Progress in seek bar mapped to [BookCondition] enum.
     *
     * @param progress in seek bar.
     * @return [BookCondition] instance corresponding to given progress.
     */
    private fun getBookCondition(progress: Int): BookCondition? {
        when (progress) {
            0 -> return BookCondition.POOR
            1 -> return BookCondition.LOOSE_BINDING
            2 -> return BookCondition.BINDING_COPY
            3 -> return BookCondition.FAIR
            4 -> return BookCondition.GOOD
            5 -> return BookCondition.FINE
            6 -> return BookCondition.NEW
        }
        return null
    }

    /**
     * [BookCondition] mapped to progress int value in Seek Bar.
     *
     * @param bookCondition [BookCondition] enum
     * @return progress int value corresponding to [BookCondition] enum.
     */
    private fun getProgressFromBookCondition(bookCondition: BookCondition): Int {
        return when (bookCondition) {
            BookCondition.POOR -> 0
            BookCondition.LOOSE_BINDING -> 1
            BookCondition.BINDING_COPY -> 2
            BookCondition.FAIR -> 3
            BookCondition.GOOD -> 4
            BookCondition.FINE -> 5
            BookCondition.NEW -> 6
        }
        return -1
    }

    /**
     * Given the progress integer value of seek bar, this method sets book condition
     * details, which include : setting book condition name, setting book condition
     * detail, and setting book condition image.
     *
     * @param progress int value for seek bar.
     */
    private fun setConditionalDetails(progress: Int) {
        val condition = getBookCondition(progress) ?: return
        when (condition) {
            BookCondition.POOR -> {
                binding!!.imageViewBookCondition.setImageDrawable(resources.getDrawable(R.drawable.poor_book_condition))
                binding!!.textViewBookCondition.setText(R.string.poor)
                binding!!.conditionDetailTextView.setText(R.string.poor_description)
            }
            BookCondition.LOOSE_BINDING -> {
                binding!!.imageViewBookCondition.setImageDrawable(resources.getDrawable(R.drawable.loose_binding))
                binding!!.textViewBookCondition.setText(R.string.losse_binding)
                binding!!.conditionDetailTextView.setText(R.string.loose_binding_description)
            }
            BookCondition.BINDING_COPY -> {
                binding!!.imageViewBookCondition.setImageDrawable(resources.getDrawable(R.drawable.binding_copy))
                binding!!.textViewBookCondition.setText(R.string.binding_copy)
                binding!!.conditionDetailTextView.setText(R.string.binding_copy_description)
            }
            BookCondition.FAIR -> {
                binding!!.imageViewBookCondition.setImageDrawable(resources.getDrawable(R.drawable.fair_book_condition))
                binding!!.textViewBookCondition.setText(R.string.fair)
                binding!!.conditionDetailTextView.setText(R.string.fair_description)
            }
            BookCondition.GOOD -> {
                binding!!.imageViewBookCondition.setImageDrawable(resources.getDrawable(R.drawable.good_book_condition))
                binding!!.textViewBookCondition.setText(R.string.good)
                binding!!.conditionDetailTextView.setText(R.string.good_description)
            }
            BookCondition.FINE -> {
                binding!!.imageViewBookCondition.setImageDrawable(resources.getDrawable(R.drawable.fine_book_condition))
                binding!!.textViewBookCondition.setText(R.string.fine)
                binding!!.conditionDetailTextView.setText(R.string.fine_description)
            }
            BookCondition.NEW -> {
                binding!!.imageViewBookCondition.setImageDrawable(resources.getDrawable(R.drawable.new_book_condition))
                binding!!.textViewBookCondition.setText(R.string.new_string)
                binding!!.conditionDetailTextView.setText(R.string.new_description)
            }
        }
    }

    /**
     * Called when result is set by called activity.
     * if result code orders to close this activity, then
     * close this activity.
     *
     * @param requestCode used to call the activity.
     * @param resultCode  used to capture result type from called activity.
     * @param data        [Intent] data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, requestCode, data)
        if (requestCode == 7 && resultCode == Constants.CLOSE_ACTIVITY) {
            setResult(Constants.CLOSE_ACTIVITY)
            finish()
        }
    }

    /**
     * Back icon clicked by user.
     * This method simply kills the activity.
     *
     * @param v view for back icon.
     */
    fun backClicked(v: View?) {
        super.onBackPressed()
    }
}