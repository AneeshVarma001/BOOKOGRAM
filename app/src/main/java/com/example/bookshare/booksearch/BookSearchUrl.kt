package com.example.bookshare.booksearch

class BookSearchUrl(bookSearchUrlBuilder: BookSearchUrlBuilder) {
   var googleBookSearchApiUrl: String? = null


    init {
        googleBookSearchApiUrl=(bookSearchUrlBuilder.googleBookSearchApiUrl)
    }

    /** Url Builder for google book api  */
    class BookSearchUrlBuilder {
         var googleBookSearchApiUrl = "https://www.googleapis.com/books/v1/volumes?"
        private var searchQueryAdded = false
        fun addSearchQuery(query: String): BookSearchUrlBuilder {
            var query = query
            query = query.replace(" ", "%20")
            googleBookSearchApiUrl = googleBookSearchApiUrl + "q=" + query
            searchQueryAdded = true
            return this
        }

        fun addStartIndex(startIndex: Int): BookSearchUrlBuilder {
            googleBookSearchApiUrl = "$googleBookSearchApiUrl&startIndex=$startIndex"
            return this
        }

        fun addMaxResults(maxResults: Int): BookSearchUrlBuilder {
            googleBookSearchApiUrl = "$googleBookSearchApiUrl&maxResults=$maxResults"
            return this
        }

        fun build(): BookSearchUrl {
            /** Searched query is not present  */
            return if (!searchQueryAdded) {
                throw UnsupportedOperationException("You forgot to put searched query while building url")
            } else {
                BookSearchUrl(this)
            }
        }
    }
}