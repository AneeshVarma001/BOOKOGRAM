package com.example.bookshare.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bookshare.R
import com.example.bookshare.databinding.ActivityBookDetailBinding
import com.example.bookshare.models.SearchedBook
import com.example.bookshare.models.UploadedBook.UploadedBookBuilder
import com.example.bookshare.util.BookUtil
import com.example.bookshare.util.BundleUtil
import com.example.bookshare.util.Constants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener

/**
 * This activity displays book details.

 */
class BookDetailActivity : AppCompatActivity() {
    private var binding: ActivityBookDetailBinding? = null

    /**
     * [SearchedBook] model.
     */
    var searchedBook: SearchedBook? = null

    /**
     * Boolean value denoting call made for uploading a book.
     */
    var isFromUpload: Boolean? = null

    /**
     * Bitmap book image.
     */
    var bookImage: Bitmap? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        /**
         * Get the dynamic elements passed on with intent or from saved instance state.
         */
        searchedBook = BundleUtil.getSearchedBookFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.SEARCHED_BOOK_KEY,
            null
        )
        isFromUpload = BundleUtil.getBooleanFromBundle(
            savedInstanceState,
            intent.extras,
            Constants.IS_FROM_UPLOAD,
            false
        )
        bookImage = BookUtil.convertByteArrayToBitmap(
            BundleUtil.getByteArrayFromBundle(
                savedInstanceState,
                intent.extras,
                Constants.BOOK_IMAGE_KEY,
                null
            )
        )
        /**
         * if book image is null, picasso was not able to load image, now load it.
         */
        if (bookImage == null && searchedBook!!.thumbnailLink!!.isNotEmpty()) {
            Glide.with(this).load(searchedBook!!.thumbnailLink).into(binding!!.bookImageView)
        } else {
            binding!!.bookImageView.setImageBitmap(bookImage)
        }
        /**
         * If call came from upload activity, then don't show need this book tile, else don't show share this book tile
         */
        if (isFromUpload!!) {
            binding!!.needBookCardView.visibility = View.GONE
        } else {
            binding!!.sharingCardView.visibility = View.GONE
        }
        /**
         * Set Searched Book Details
         */
        setTitle(searchedBook!!.title, searchedBook!!.subTitle)
        setAuthors(searchedBook!!.authors)
        setDescription(searchedBook!!.description)
        setPublishingDetails(searchedBook!!.publisher, searchedBook!!.publishedDate)
        /**
         * Upload book Yes clicked
         */
        binding!!.yesTextView.setOnClickListener {
            val intent = Intent(this@BookDetailActivity, BookConditionActivity::class.java)
            val uploadedBook = UploadedBookBuilder()
                .addSearchedBook(searchedBook)
                .addBookImage(BookUtil.convertBitmapToByteArray(bookImage)!!)
                .build()
            intent.putExtra(Constants.UPLOADED_BOOK_KEY, uploadedBook)
            startActivityForResult(intent, 6)
        }
        /**
         * Find book Yes clicked
         */
        binding!!.yesFindTextView.setOnClickListener {
            val intent = Intent(this@BookDetailActivity, FindBookInFriendActivity::class.java)
            intent.putExtra(Constants.SEARCHED_BOOK_TITLE, searchedBook!!.title)
            startActivity(intent)
        }
        /**
         * find book No clicked
         */
        binding!!.noFindTextView.setOnClickListener {
            val slideAnimation =
                AnimationUtils.loadAnimation(this@BookDetailActivity, R.anim.slide_right_to_left)
            slideAnimation.fillAfter = true
            binding!!.needBookCardView.startAnimation(slideAnimation)
            slideAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    binding!!.needBookCardView.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        /**
         * upload book No clicked
         */
        binding!!.noTextView.setOnClickListener {
            val slideAnimation =
                AnimationUtils.loadAnimation(this@BookDetailActivity, R.anim.slide_right_to_left)
            slideAnimation.fillAfter = true
            binding!!.sharingCardView.startAnimation(slideAnimation)
            slideAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    binding!!.sharingCardView.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        /**
         * app bar offset listener
         */
        binding!!.appbar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                /**
                 * For a fixed scroll range, display toolbar text title, else hide toolbar text.
                 */
                if (scrollRange + verticalOffset >= -1 * resources.getDimension(R.dimen.density_56dp) && scrollRange + verticalOffset <= resources.getDimension(
                        R.dimen.density_56dp
                    )
                ) {
                    /**
                     * Show title on toolbar
                     */
                    setToolbarTitle(searchedBook!!.title!!, searchedBook!!.subTitle!!)
                    isShow = true
                } else if (isShow) {
                    /**
                     * Empty text in toolbar title.
                     */
                    setToolbarTitle("", "")
                    isShow = false
                }
            }
        })
    }

    /**
     * [Picasso] target for loading bitmap images.
     */
    /*    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bookImage = bitmap;
            binding.bookImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };*/
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        /**
         * Saving dynamic elements for this activity.
         */
        savedInstanceState.putParcelable(Constants.SEARCHED_BOOK_KEY, searchedBook)
        savedInstanceState.putBoolean(Constants.IS_FROM_UPLOAD, isFromUpload!!)
        savedInstanceState.putByteArray(
            Constants.BOOK_IMAGE_KEY,
            BookUtil.convertBitmapToByteArray(bookImage)
        )
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
            binding!!.titleTextView.text = title
            //            binding.fullTitleTextView.setText(title);
        } else {
            binding!!.titleTextView.text = String.format("%s:%s", title, subTitle)
            //            binding.fullTitleTextView.setText(String.format("%s:%s", title, subTitle));
        }
    }

    /**
     * Sets title in the toolbar.
     *
     * @param title of the book
     * @param subTitle of the book
     */
    fun setToolbarTitle(title: String, subTitle: String) {
        if (title.length == 0
            && subTitle.length == 0
        ) {
            binding!!.toolbarTitleTextView.text = ""
        }
        if (subTitle.length == 0) {
            binding!!.toolbarTitleTextView.text = title
        } else {
            binding!!.toolbarTitleTextView.text = String.format("%s:%s", title, subTitle)
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
            binding!!.authorsCardView.visibility = View.GONE
        } else {
            binding!!.authorsTextView.text = TextUtils.join(",", authors)
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
            binding!!.descriptionCardView.visibility = View.GONE
        } else {
            binding!!.descriptionTextView.text = description
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
            binding!!.publisherCardView.visibility = View.GONE
        } else {
            if (publishingDate == null
                || publishingDate.length == 0
            ) {
                binding!!.publishingDetailsTextView.text = publisher
            } else {
                binding!!.publishingDetailsTextView.text =
                    String.format("%s - %s", publisher, publishingDate)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, requestCode, data)
        if (requestCode == 6 && resultCode == Constants.CLOSE_ACTIVITY) {
            setResult(Constants.CLOSE_ACTIVITY)
            finish()
        }
    }

    /**
     * Back icon pressed by user
     *
     * @param v view for the back icon
     */
    fun backClicked(v: View?) {
        super.onBackPressed()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }
}