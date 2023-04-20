package com.example.bookshare.booksearch

import com.example.bookshare.booksearch.BookSearchRequestHandler.BookSearchRequestEvents
import com.example.bookshare.booksearch.BookSearchResponseHandler.BookSearchResponseEvents
import com.example.bookshare.models.BookSearchFailure
import com.example.bookshare.models.SearchedBook

class BookSearchHandler {
    private var bookSearchRequestHandler: BookSearchRequestHandler? = null
    private var bookSearchResponseHandler: BookSearchResponseHandler? = null
    private var bookSearchRequestListener: BookSearchRequestListener? = null
    private var bookSearchResponseListener: BookSearchResponseListener? = null
    private var query: String? = null
    private var searchHasCompleted = false
    var bookSearchEvents: BookSearchEvents? = null

    /** Initialize objects in constructor  */
    init {
        init()
    }

    private fun init() {
        bookSearchRequestHandler = BookSearchRequestHandler()
        bookSearchRequestListener = BookSearchRequestListener()
        bookSearchRequestHandler!!.bookSearchRequestEvents = bookSearchRequestListener
        bookSearchResponseHandler = BookSearchResponseHandler()
        bookSearchResponseListener = BookSearchResponseListener()
        bookSearchResponseHandler!!.setBookSearchResponseEvents(bookSearchResponseListener)
    }

    /** Initiate request-response handling for the given search query  */
    fun initiateQuery(query: String?) {
        /** Clear previous objects and initialize again  */
        clean()
        init()
        /** save the query for further use  */
        this.query = query
        searchHasCompleted = false
        /** Start the request  */
        bookSearchRequestHandler!!.fetchBookData(query!!)
    }

    /** Tries to fetch more data for same request query term  */
    fun fetchNextBatch() {
        /** If there are still pages left to be fetched  */
        if (!searchHasCompleted) {
            /** Need to increase start index of request handler for fetching next set of data  */
            bookSearchRequestHandler!!.startIndex =
                bookSearchRequestHandler!!.startIndex + bookSearchRequestHandler!!.maxResults
            /** Start the request  */
            bookSearchRequestHandler!!.fetchBookData(query!!)
        }
    }

    /** Retries to fetch more data for same request query term  */
    fun retryFetchBatch() {
        /** Need to retry fetching the batch, which previously might have failed  */
        bookSearchRequestHandler!!.fetchBookData(query!!)
    }

    /** Listener for book search response events  */
    private inner class BookSearchResponseListener : BookSearchResponseEvents {
        override fun onBookSearchResponseSuccessfullyParsed(searchedBookArrayList: ArrayList<SearchedBook?>?) {
            /** If there is an empty array list, this means nothing found  */
            if (searchedBookArrayList == null || searchedBookArrayList.size == 0) {
                searchHasCompleted = true
                /** Notify that search has completed with no data  */
                if (bookSearchEvents != null) {
                    bookSearchEvents!!.onBookSearchCompleted(true, ArrayList())
                }
            } else {
                /** Data is present  */
                /** Return the whole list  */
                if (bookSearchEvents != null) {
                    bookSearchEvents!!.onBookSearchCompleted(false, searchedBookArrayList)
                }
            }
        }

        override fun onBookSearchResponseParsedFailed() {
            /** Failed to parse  */
            if (bookSearchEvents != null) {
                bookSearchEvents!!.onBookSearchFailed(BookSearchFailure.RESPONSE_FAILURE)
            }
        }

        override fun onBookSearchResponseItemsBodyEmpty() {
            searchHasCompleted = true
            /** Notify that search has completed with no data  */
            if (bookSearchEvents != null) {
                bookSearchEvents!!.onBookSearchCompleted(true, ArrayList())
            }
        }
    }

    /** Listener for book search request events  */
    private inner class BookSearchRequestListener : BookSearchRequestEvents {
        override fun onBookSearchRequestCompleted(data: String?) {
            bookSearchResponseHandler!!.handleBookSearchResponse(data!!)
        }

        override fun onBookSearchRequestFailed() {
            if (bookSearchEvents != null) {
                bookSearchEvents!!.onBookSearchFailed(BookSearchFailure.REQUEST_FAILURE)
            }
        }
    }

    interface BookSearchEvents {
        fun onBookSearchFailed(bookSearchFailure: BookSearchFailure?)
        fun onBookSearchCompleted(
            searchHasCompleted: Boolean,
            searchedBookArrayList: ArrayList<SearchedBook?>?
        )
    }

    private fun clean() {
        bookSearchRequestHandler = null
        bookSearchRequestListener = null
        bookSearchResponseHandler = null
        bookSearchResponseListener = null
    }
}