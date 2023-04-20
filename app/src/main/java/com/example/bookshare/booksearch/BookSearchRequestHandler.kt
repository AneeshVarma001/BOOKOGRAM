package com.example.bookshare.booksearch

import com.example.bookshare.booksearch.AsyncBookDataFetch.TaskCompleteEvent
import com.example.bookshare.booksearch.BookSearchUrl.BookSearchUrlBuilder
import com.example.bookshare.util.FLog.i

class BookSearchRequestHandler {
    var startIndex = 0
    var maxResults = 10
    var bookSearchRequestEvents: BookSearchRequestEvents? = null

    /** Only this method can be called from an outer class,
     * it handles all fetch related preparations and executions.  */
    fun fetchBookData(query: String) {
        val url = getRequestUrl(query)
        if (url == "") {
            /** Call back for book search failed event  */
            if (bookSearchRequestEvents != null) {
                bookSearchRequestEvents!!.onBookSearchRequestFailed()
            }
            return
        }
        /** logging complete url  */
        i(this, url)
        /** async call for fetching google book api data  */
        val asyncBookDataFetch = AsyncBookDataFetch()
        asyncBookDataFetch.setTaskCompleteEvent(object : TaskCompleteEvent {
            override fun onTaskComplete(result: String?) {
                /** Call back for book search complete event  */
                if (bookSearchRequestEvents != null) {
                    bookSearchRequestEvents!!.onBookSearchRequestCompleted(result)
                }
            }

            override fun onTaskFailed() {
                /** Call back for book search failed event  */
                if (bookSearchRequestEvents != null) {
                    bookSearchRequestEvents!!.onBookSearchRequestFailed()
                }
            }
        })
        asyncBookDataFetch.execute(url)
    }

    /** Tries to build google book api search url, given a query string.  */
    private fun getRequestUrl(query: String): String {
        val bookSearchUrl: BookSearchUrl
        bookSearchUrl = try {
            BookSearchUrlBuilder()
                .addSearchQuery(query)
                .addStartIndex(startIndex)
                .addMaxResults(maxResults)
                .build()
        } catch (e: Exception) {
            return ""
        }
        return bookSearchUrl!!.googleBookSearchApiUrl!!
    }

    /** Callback for every events that can happen while requesting for a url  */
    interface BookSearchRequestEvents {
        fun onBookSearchRequestCompleted(data: String?)
        fun onBookSearchRequestFailed()
    }
}