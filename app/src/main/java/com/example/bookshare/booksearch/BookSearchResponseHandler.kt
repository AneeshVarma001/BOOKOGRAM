package com.example.bookshare.booksearch

import com.example.bookshare.models.BookImageType
import com.example.bookshare.models.SearchedBook
import com.example.bookshare.models.SearchedBook.SearchedBookBuilder
import com.example.bookshare.util.BookUtil.preferredImageLink
import com.example.bookshare.util.Constants
import org.json.JSONArray
import org.json.JSONObject

class BookSearchResponseHandler {
    /**
     * ArrayList for all `SearchedBook` data items parsed from json string
     */
    private var searchedBookArrayList: ArrayList<SearchedBook?>? = null
    private var bookSearchResponseEvents: BookSearchResponseEvents? = null

    /**
     * Handles Book Search Response data
     */
    fun handleBookSearchResponse(responseString: String) {
        var jsonArray: JSONArray? = null
        jsonArray = try {
            /** Try to get items from Json object  */
            getItemsJSONArrayFromString(responseString)
        } catch (e: Exception) {
            /** Notify that parsing failed  */
            if (bookSearchResponseEvents != null) {
                bookSearchResponseEvents!!.onBookSearchResponseParsedFailed()
            }
            return
        }
        if (jsonArray == null || jsonArray.length() == 0) {
            /** Notify that empty 'items' body found  */
            if (bookSearchResponseEvents != null) {
                bookSearchResponseEvents!!.onBookSearchResponseItemsBodyEmpty()
            }
            return
        }
        /** Parse 'items' further and populate arrayList for searched books  */
        /** Call book search response successfully parsed  */
        try {
            /** Try to parse further into 'items' category  */
            parseResponseItems(jsonArray)
            /** Notify successfully parsed  */
            bookSearchResponseEvents!!.onBookSearchResponseSuccessfullyParsed(searchedBookArrayList)
        } catch (e: Exception) {
            /** Notify that parsing failed  */
            if (bookSearchResponseEvents != null) {
                bookSearchResponseEvents!!.onBookSearchResponseParsedFailed()
            }
        }
    }

    /**
     * Helps parse json response, given json array denoting 'items'
     */
    @Throws(Exception::class)
    private fun parseResponseItems(jsonArray: JSONArray) {
        /** Loop through every 'items' element and get searched book details  */
        searchedBookArrayList = ArrayList()
        for (i in 0 until jsonArray.length()) {
            val searchedBook = parseSingleItemAndPrepareSearchedBook(jsonArray.getJSONObject(i))
            if (searchedBook != null) {
                searchedBookArrayList!!.add(searchedBook)
            }
        }
    }

    /**
     * Fetches items array present in json string
     */
    @Throws(Exception::class)
    private fun getItemsJSONArrayFromString(jsonString: String): JSONArray? {
        val jsonObject = JSONObject(jsonString)
        return if (jsonObject.has(Constants.BOOK_API_RESPONSE_ITEMS)) {
            jsonObject[Constants.BOOK_API_RESPONSE_ITEMS] as JSONArray
        } else {
            null
        }
    }

    /**
     * This method helps parse the JSON object returned by
     * google books api.
     *
     *
     * Required Sub-part of response type from google book api :
     *
     *
     * 'kind' : 'book#volume' (ignore book shelves)
     * "volumeInfo": {
     * "title": string,
     * "subtitle": string,
     * "authors": [ list of strings ],
     * "publisher": string,
     * "publishedDate": string,
     * "description": string,
     * "industryIdentifiers": [{
     * "type": string,
     * "identifier": string
     * }
     * "imageLinks": {
     * "smallThumbnail": string,
     * "thumbnail": string,
     * "small": string,
     * "medium": string,
     * "large": string,
     * "extraLarge": string
     * },
     * }
     */
    @Throws(Exception::class)
    fun parseSingleItemAndPrepareSearchedBook(jsonObject: JSONObject): SearchedBook? {
        /** Does not have a 'kind' tag, data could be anything, return null  */
        val kindForResult: String
        kindForResult = if (jsonObject.has(Constants.BOOK_API_RESPONSE_KIND)) {
            jsonObject.getString(Constants.BOOK_API_RESPONSE_KIND)
        } else {
            return null
        }
        /** Not a book volume  */
        if (kindForResult != "books#volume") {
            return null
        }
        /** Volume info from 'items'  */
        val volumeInfo: JSONObject
        volumeInfo =
            if (jsonObject.has(Constants.BOOK_API_RESPONSE_VOLUME_INFO)) {
                jsonObject.getJSONObject(Constants.BOOK_API_RESPONSE_VOLUME_INFO) as JSONObject
            } else {
                return null
            }
        /** Title  */
        val title: String
        title = if (volumeInfo.has(Constants.BOOK_API_RESPONSE_TITLE)) {
            volumeInfo.getString(Constants.BOOK_API_RESPONSE_TITLE)
        } else {
            return null
        }
        /** Subtitle  */
        var subtitle: String? = ""
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_SUBTITLE)) {
            subtitle = volumeInfo.getString(Constants.BOOK_API_RESPONSE_SUBTITLE)
        }
        /** Authors  */
        val authors: JSONArray
        authors = if (volumeInfo.has(Constants.BOOK_API_RESPONSE_AUTHORS)) {
            volumeInfo.getJSONArray(Constants.BOOK_API_RESPONSE_AUTHORS)
        } else {
            return null
        }
        val authorsList = ArrayList<String?>()
        for (j in 0 until authors.length()) {
            val author = authors.getString(j)
            authorsList.add(author)
        }
        /** Publisher  */
        var publisher: String? = ""
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_PUBLISHER)) {
            publisher = volumeInfo.getString(Constants.BOOK_API_RESPONSE_PUBLISHER)
        }
        /** PublishedDate  */
        var publishedDate: String? = ""
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_PUBLISHED_DATE)) {
            publishedDate = volumeInfo.getString(Constants.BOOK_API_RESPONSE_PUBLISHED_DATE)
        }
        /** Description  */
        var description: String? = ""
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_DESCRIPTION)) {
            description = volumeInfo.getString(Constants.BOOK_API_RESPONSE_DESCRIPTION)
        }
        /** ISBN  */
        var industryIdentifier = ""
        var identifiersJSONArray: JSONArray? = null
        if (volumeInfo.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER)) {
            identifiersJSONArray =
                volumeInfo.getJSONArray(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER)
        }
        var j = 0
        while (identifiersJSONArray != null && j < identifiersJSONArray.length()) {
            val identifierJsonObject = identifiersJSONArray.getJSONObject(j)
            var type = ""
            if (identifierJsonObject.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE)) {
                type =
                    identifierJsonObject.getString(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE)
            }
            if (type == Constants.BOOK_API_RESPONSE_ISBN_13) {
                if (identifierJsonObject.has(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER)) {
                    industryIdentifier =
                        identifierJsonObject.getString(Constants.BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER)
                }
            }
            j++
        }
        /** ImageLink  */
        val imageLinksJSONObject: JSONObject
        imageLinksJSONObject = if (volumeInfo.has(Constants.BOOK_API_RESPONSE_IMAGELINKS)) {
            volumeInfo[Constants.BOOK_API_RESPONSE_IMAGELINKS] as JSONObject
        } else {
            JSONObject()
        }
        val bookImageTypeStringHashMap = HashMap<BookImageType, String?>()
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL)) {
            bookImageTypeStringHashMap[BookImageType.SMALL_THUMBNAIL] =
                imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL)
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL)) {
            bookImageTypeStringHashMap[BookImageType.THUMBNAIL] =
                imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL)
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALL)) {
            bookImageTypeStringHashMap[BookImageType.SMALL] =
                imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_SMALL)
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_MEDIUM)) {
            bookImageTypeStringHashMap[BookImageType.MEDIUM] =
                imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_MEDIUM)
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_LARGE)) {
            bookImageTypeStringHashMap[BookImageType.LARGE] =
                imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_LARGE)
        }
        if (imageLinksJSONObject.has(Constants.BOOK_API_RESPONSE_IMAGELINKS_XLARGE)) {
            bookImageTypeStringHashMap[BookImageType.XLARGE] =
                imageLinksJSONObject.getString(Constants.BOOK_API_RESPONSE_IMAGELINKS_XLARGE)
        }
        val thumbnail = preferredImageLink(bookImageTypeStringHashMap)
        /** return the whole make over for searched book  */
        return SearchedBookBuilder()
            .addTitle(title)
            .addSubtitle(subtitle)
            .addDescription(description)
            .addAuthors(authorsList)
            .addIndustryIdentifier(industryIdentifier)
            .addPublishedDate(publishedDate)
            .addPublisher(publisher)
            .addThumbnailLink(thumbnail)
            .build()
    }

    fun setBookSearchResponseEvents(bookSearchResponseListener: BookSearchResponseEvents?) {
        bookSearchResponseEvents = bookSearchResponseListener
    }

    /**
     * Callback events for each response parsing mechanism timeline
     */
    interface BookSearchResponseEvents {
        fun onBookSearchResponseSuccessfullyParsed(searchedBookArrayList: ArrayList<SearchedBook?>?)
        fun onBookSearchResponseParsedFailed()
        fun onBookSearchResponseItemsBodyEmpty()
    }
}