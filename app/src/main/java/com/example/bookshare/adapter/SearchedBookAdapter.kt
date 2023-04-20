package com.example.bookshare.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.bookshare.R
import com.example.bookshare.models.DotsState
import com.example.bookshare.models.SearchedBook
import pl.tajchert.sample.DotsTextView

class SearchedBookAdapter(
    childLayoutResId: Int,
    searchedBookArrayList: ArrayList<SearchedBook?>?,
    context: Context?,
    footerAdded: Boolean,
    footerLayoutResId: Int
) : BaseAdapter<SearchedBook?>(
    childLayoutResId,
    searchedBookArrayList!!,
    context!!,
    footerAdded,
    footerLayoutResId
) {
    var moreDataRequired: MoreDataRequired? = null
    private var retryButtonVisibility = 0
    private var retryTextViewVisibility = 0
    private var dotsTextViewVisibility = 0
    private var dotsState: DotsState? = null
    private var defaultMessage: String? = null
    private var alteredMessage: String? = null

    /** Constructor for Search Book Adapter  */
    init {
        if (context != null) {
            defaultMessage = context.resources.getString(R.string.could_not_complete_search_query)
        }
    }

    override fun onCreateInitializeDataObjectHolder(v: View?): DataObjectHolder? {
        return SearchedBookViewHolder(v)
    }

    override fun onCreateFooterInitializeDataObjectHolder(v: View?): DataObjectHolder? {
        return SearchedBookFooterViewHolder(v)
    }

    override fun onBindItemViewHolder(
        holder: DataObjectHolder?,
        position: Int,
        searchedBookValueAtGivenPosition: SearchedBook?
    ) {
        if (searchedBookValueAtGivenPosition != null) {
            val searchedBookViewHolder = holder as SearchedBookViewHolder?
            /** Title  */
            if (searchedBookViewHolder!!.searchBookTitleView != null) {
                searchedBookViewHolder.searchBookTitleView!!.text =
                    searchedBookValueAtGivenPosition.title
            }
            /** Author  */
            if (searchedBookViewHolder.searchBookAuthorView != null) {
                searchedBookViewHolder.searchBookAuthorView!!.text =
                    TextUtils.join(",", searchedBookValueAtGivenPosition.authors!!)
            }
            /** ISBN  */
            if (searchedBookViewHolder.searchBookISBN != null) {
                searchedBookViewHolder.searchBookISBN!!.text =
                    context.getString(R.string.isbn) + searchedBookValueAtGivenPosition.industryIdentifier
            }
            /** Thumbnail  */
            val thumbnailUrl = searchedBookValueAtGivenPosition.thumbnailLink
            if (context != null && searchedBookViewHolder.searchBookImageView != null) {
                searchedBookViewHolder.searchBookImageView!!.background =
                    context.resources.getDrawable(R.drawable.loading)
            }
            if (thumbnailUrl != null && thumbnailUrl != "" && searchedBookViewHolder.searchBookImageView != null && context != null) {
                Glide.with(context)
                    .load(thumbnailUrl)
                    .error(R.drawable.no_preview_available)
                    .into(searchedBookViewHolder.searchBookImageView!!)
            } else if (thumbnailUrl == null || thumbnailUrl == "" && context != null) {
                searchedBookViewHolder.searchBookImageView!!.background =
                    context.resources.getDrawable(R.drawable.no_preview_available)
            }
        }
        /** Reached close to end ask for more data demand  */
        if (Math.abs(itemCount - position) <= 3) {
            if (moreDataRequired != null) {
                moreDataRequired!!.onDataDemanded()
            }
        }
    }

    override fun onBindFooterItemViewHolder(holder: DataObjectHolder?) {
        val searchedBookFooterViewHolder = holder as SearchedBookFooterViewHolder?
        searchedBookFooterViewHolder!!.retryButton!!.visibility = retryButtonVisibility
        searchedBookFooterViewHolder.retryTextView!!.visibility = retryTextViewVisibility
        searchedBookFooterViewHolder.dotsTextView!!.visibility = dotsTextViewVisibility
        if (context != null) {
            searchedBookFooterViewHolder.retryTextView!!.text = defaultMessage
        }
        if (dotsState === DotsState.START) {
            searchedBookFooterViewHolder.dotsTextView!!.start()
        } else if (dotsState === DotsState.STOP) {
            searchedBookFooterViewHolder.dotsTextView!!.stop()
        }
        /** If some alternate message is set by adapter, display that first  */
        if (alteredMessage != null && alteredMessage!!.length > 0) {
            searchedBookFooterViewHolder.retryTextView!!.visibility = View.VISIBLE
            searchedBookFooterViewHolder.retryTextView!!.text = alteredMessage
            alteredMessage = ""
        }
    }

    /** Add Multiple items at the end  */
    override fun addMultipleItems(searchedBookArrayList: ArrayList<SearchedBook?>?) {
        /** If empty data is given, ask for more data immediately  */
        if (searchedBookArrayList == null || searchedBookArrayList.size == 0) {
            if (moreDataRequired != null) {
                moreDataRequired!!.onDataDemanded()
            }
        } else {
            super.addMultipleItems(searchedBookArrayList)
        }
    }

    /** View holder for Searched Book Adapter  */
    inner class SearchedBookViewHolder
    /** Constructor with root view  */
        (itemView: View?) : DataObjectHolder(itemView) {
        var searchBookImageView: ImageView? = null
        var searchBookTitleView: TextView? = null
        var searchBookAuthorView: TextView? = null
        var searchBookISBN: TextView? = null
        override fun initialize(rootView: View?) {
            if (rootView != null) {
                searchBookImageView =
                    rootView.findViewById<View>(R.id.search_result_book_image_view) as ImageView
                searchBookTitleView =
                    rootView.findViewById<View>(R.id.search_result_book_title) as TextView
                searchBookAuthorView =
                    rootView.findViewById<View>(R.id.search_result_book_authors) as TextView
                searchBookISBN =
                    rootView.findViewById<View>(R.id.search_result_book_isbn) as TextView
            }
        }
    }

    /** Footer View holder for Searched Book Adapter  */
    inner class SearchedBookFooterViewHolder
    /** Constructor with root view  */
        (itemView: View?) : DataObjectHolder(itemView) {
        var retryButton: Button? = null
        var retryTextView: TextView? = null
        var dotsTextView: DotsTextView? = null
        override fun initialize(rootView: View?) {
            if (rootView != null) {
                retryButton = rootView.findViewById<View>(R.id.retry_button_search_book) as Button
                retryTextView = rootView.findViewById<View>(R.id.retry_text_search_book) as TextView
                dotsTextView = rootView.findViewById<View>(R.id.dots_search_book) as DotsTextView
            }
        }
    }

    /** Updates footer for adapter  */
    fun updateFooter(
        retryButtonVisibility: Int,
        retryTextViewVisibility: Int,
        dotsTextViewVisibility: Int,
        dotsState: DotsState?,
        alteredMessage: String?
    ) {
        this.retryButtonVisibility = retryButtonVisibility
        this.retryTextViewVisibility = retryTextViewVisibility
        this.dotsTextViewVisibility = dotsTextViewVisibility
        this.dotsState = dotsState
        if (alteredMessage != null && alteredMessage.isNotEmpty()) {
            this.alteredMessage = alteredMessage
        }
        notifyDataSetChanged()
    }

    interface MoreDataRequired {
        fun onDataDemanded()
    }
}