package com.example.bookshare.activityhelpers

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshare.R
import com.example.bookshare.adapter.BaseAdapter
import com.example.bookshare.adapter.SearchedBookAdapter
import com.example.bookshare.adapter.SearchedBookAdapter.MoreDataRequired
import com.example.bookshare.booksearch.BookSearchHandler
import com.example.bookshare.booksearch.BookSearchHandler.BookSearchEvents
import com.example.bookshare.models.BookSearchFailure
import com.example.bookshare.models.DotsState
import com.example.bookshare.models.SearchedBook
import com.example.bookshare.util.BookUtil
import com.example.bookshare.util.NetworkUtil

class SearchActivityHelper(private val context: Context?) {
    private var itemClickEvent: ItemClickEvent? = null
    private var searchedBookAdapter: SearchedBookAdapter? = null
    private val bookSearchHandler: BookSearchHandler?

    /** Constructor for this helper class  */
    init {
        bookSearchHandler = BookSearchHandler()
        bookSearchHandler.bookSearchEvents = BookSearchListener()
    }

    /**
     * This will set recycler view with fixed size and having linear layout manager.
     * Also, sets the adapter and handle click events.
     *
     * @param recyclerView attached to the calling activity
     */
    fun linkRecyclerViewAndAdapter(recyclerView: RecyclerView?) {
        searchedBookAdapter = SearchedBookAdapter(
            R.layout.search_recycler_view_item,
            ArrayList(),
            context,
            true,
            R.layout.search_recycler_view_footer_item
        )
        if (recyclerView == null) {
            return
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = searchedBookAdapter
        /** Demand for more data  */
        searchedBookAdapter!!.moreDataRequired =
            object : MoreDataRequired {
                override fun onDataDemanded() {
                    bookSearchHandler?.fetchNextBatch()
                }
            }
        /** Click Events  */
        searchedBookAdapter!!.setRecyclerViewAdapterEvents(object: BaseAdapter.RecyclerViewAdapterEvents{
            override fun onItemClick(position: Int, v: View?) {
                if (itemClickEvent != null) {
                    val transitionImageView =
                        v!!.findViewById<View>(R.id.search_result_book_image_view) as ImageView
                    transitionImageView.transitionName =
                        context!!.getString(R.string.common_image_transition_view)
                    itemClickEvent!!.onItemClicked(
                        transitionImageView,
                        searchedBookAdapter!!.getItemAtPosition(position)
                    )
                }

            }
        })

        /** Update Footer  */
        searchedBookAdapter!!.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "")
    }

    /** Hide retry button, retry text, and show still dots, also update adapter; whenever search query is empty  */
    fun handleEmptyQueryEvent() {
        /** Change the visibility of retry View group  */
        searchedBookAdapter!!.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.STOP, "")
        /** Delete all items from adapter  */
        if (searchedBookAdapter != null) {
            searchedBookAdapter!!.deleteAllItems()
        }
    }

    /** There is some query made by user, hide retry button, retry text, and show dots animating  */
    fun handleNewQueryEvent(query: String?) {
        /** Change retry view group visibility  */
        searchedBookAdapter!!.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.START, "")
        /** Delete all items from adapter  */
        if (searchedBookAdapter != null) {
            searchedBookAdapter!!.deleteAllItems()
        }
        /** Initiate book search  */
        bookSearchHandler?.initiateQuery(query)
    }

    /** Some failure occurred while searching books online, show retry button, retry text, and hide dots  */
    fun handleSearchQueryFailedEvent(failureMessage: String?) {
        /** Change retry view group visibility  */
        searchedBookAdapter!!.updateFooter(
            View.VISIBLE,
            View.VISIBLE,
            View.GONE,
            DotsState.STOP,
            failureMessage
        )
    }

    /** If user demands retrying, retry for next batch  */
    fun retrySearching() {
        /** Change visibility of retry view group  */
        searchedBookAdapter!!.updateFooter(View.GONE, View.GONE, View.VISIBLE, DotsState.START, "")
        /** Ask handler to retry  */
        bookSearchHandler?.retryFetchBatch()
    }

    fun setItemClickEvent(recyclerViewItemClick: ItemClickEvent?) {
        itemClickEvent = recyclerViewItemClick
    }

    /** Class for book searching and handing its events  */
    internal inner class BookSearchListener : BookSearchEvents {
        override fun onBookSearchFailed(bookSearchFailure: BookSearchFailure?) {
            if (context != null) {
                var message = ""
                /** Check for internet connection  */
                if (!NetworkUtil.isNetworkConnected(context)) {
                    message = context.getString(R.string.seems_like_no_internet_connection)
                }
                /** ask failure handler to take from here  */
                handleSearchQueryFailedEvent(message)
            }
        }

        override fun onBookSearchCompleted(
            searchHasCompleted: Boolean,
            searchedBookArrayList: ArrayList<SearchedBook?>?
        ) {
            /** Got the data, notify adapter  */
            if (searchedBookArrayList != null && searchedBookArrayList.size > 0) {
                searchedBookAdapter!!.addMultipleItems(
                    BookUtil.sterilizeSearchedBooks(
                        searchedBookArrayList
                    )!!
                )
            }
            /** if search has completed, remove dot, retry text, retry button  */
            if (searchHasCompleted && context != null) {
                /** If received output has zero size and adapter also has no data (except footer), this means : 'nothing found'  */
                if ((searchedBookArrayList == null || searchedBookArrayList.size == 0) && searchedBookAdapter!!.itemCount == 1) {
                    searchedBookAdapter!!.updateFooter(
                        View.GONE,
                        View.GONE,
                        View.VISIBLE,
                        DotsState.STOP,
                        context.resources.getString(R.string.nothing_found_for_the_given_keyword)
                    )
                } else {
                    searchedBookAdapter!!.updateFooter(
                        View.GONE,
                        View.GONE,
                        View.VISIBLE,
                        DotsState.STOP,
                        ""
                    )
                }
            }
        }
    }

    /** Callback for item click event  */
    interface ItemClickEvent {
        fun onItemClicked(transitionImageView: ImageView?, searchedBook: SearchedBook?)
    }
}