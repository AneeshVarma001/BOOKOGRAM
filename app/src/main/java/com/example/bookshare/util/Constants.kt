package com.example.bookshare.util

object Constants {
    /** Book Api related constants  */
    const val BOOK_API_RESPONSE_ITEMS = "items"
    const val BOOK_API_RESPONSE_VOLUME_INFO = "volumeInfo"
    const val BOOK_API_RESPONSE_TITLE = "title"
    const val BOOK_API_RESPONSE_AUTHORS = "authors"
    const val BOOK_API_RESPONSE_SUBTITLE = "subtitle"
    const val BOOK_API_RESPONSE_PUBLISHER = "publisher"
    const val BOOK_API_RESPONSE_PUBLISHED_DATE = "publishedDate"
    const val BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER = "industryIdentifiers"
    const val BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_TYPE = "type"
    const val BOOK_API_RESPONSE_INDUSTRY_IDENTIFIER_IDENTIFIER = "identifier"
    const val BOOK_API_RESPONSE_DESCRIPTION = "description"
    const val BOOK_API_RESPONSE_ISBN_13 = "ISBN_13"
    const val BOOK_API_RESPONSE_IMAGELINKS = "imageLinks"
    const val BOOK_API_RESPONSE_IMAGELINKS_SMALLTHUMBNAIL = "smallThumbnail"
    const val BOOK_API_RESPONSE_IMAGELINKS_THUMBNAIL = "thumbnail"
    const val BOOK_API_RESPONSE_IMAGELINKS_SMALL = "small"
    const val BOOK_API_RESPONSE_IMAGELINKS_MEDIUM = "medium"
    const val BOOK_API_RESPONSE_IMAGELINKS_LARGE = "large"
    const val BOOK_API_RESPONSE_IMAGELINKS_XLARGE = "extraLarge"
    const val BOOK_API_RESPONSE_KIND = "kind"

    /** Layout Manager key for parcel object  */
    const val SAVED_LAYOUT_MANAGER_KEY = "SAVED_LAYOUT_MANAGER_KEY"

    /** Firebase helper constants  */
    const val feedbackPrefs = "Fiboku.feedbackPrefs"
    const val acraPrefs = "Fiboku.acraPrefs"
    const val feedbackMap = "FEEBACKMAP"
    const val acraMap = "ACRAMAP"
    const val agentName = "AGENTNAME"
    const val agentPrefs = "Fiboku.agentPrefs"
    const val emailPrefs = "Fiboku.emailPrefs"
    const val emailName = "EMAILNAME"
    const val passwordPrefs = "Fiboku.passwordPrefs"
    const val passwordName = "PASSWORDNAME"
    const val phoneNumberPrefs = "Fiboku.phoneNumberPrefs"
    const val phoneNumberName = "PHONENUMBERNAME"
    const val uidPrefs = "Fiboku.uidPrefs"
    const val uidName = "UIDNAME"
    const val RPNs = "RPNs"
    const val BOOKs = "books"
    const val USERs = "users"
    const val NAME = "Name"
    const val MESSAGES = "Messages"

    /** Intent Keys  */
    const val USER_DATA_KEY = "USERDATAKEY"
    const val ISBN_KEY = "ISBN_KEY"
    const val IS_FROM_UPLOAD = "IS_FROM_UPLOAD"
    const val RESULT_OK = 9
    const val RESULT_FAILURE = 8
    const val RESULT_CANCELLED = 7
    const val ISBN_REQUEST = 6
    const val REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 5
    const val SEARCHED_BOOK_KEY = "SEARCHED_BOOK_KEY"
    const val UPLOADED_BOOK_KEY = "UPLOADED_BOOK_KEY"
    const val BOOK_IMAGE_KEY = "BOOK_IMAGE_KEY"
    const val CLOSE_ACTIVITY = 0
    const val UPDATE_REQUIRED = "UPDATE_REQUIRED"
    const val BOOK_ID = "BOOK_ID"
    const val MESSSAGE_EMAIL = "MESSAGE_EMAIL"
    const val SEARCHED_BOOK_TITLE = "SEARCHED_BOOK_TITLE"

    /** Activity ids  */
    const val homeActivity = 0
    const val uploadedBooksActivity = 1
    const val messagesActivity = 2
    const val connectionsActivity = 3
    const val feedbackActivity = 4
    const val aboutActivity = 5

    /** Drawer item ids  */
    const val homeDrawerItemCode = 0
    const val uploadedBooksDrawerItemCode = 1
    const val messagesDrawerItemCode = 2
    const val connectionsDrawerItemCode = 3
    const val feedbackDrawerItemCode = 4
    const val aboutDrawerItemCode = 5
    const val logoutDrawerItemCode = 6
}