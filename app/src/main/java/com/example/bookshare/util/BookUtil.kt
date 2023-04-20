package com.example.bookshare.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.bookshare.models.BookCondition
import com.example.bookshare.models.BookImageType
import com.example.bookshare.models.SearchedBook
import java.io.ByteArrayOutputStream
import java.util.*

object BookUtil {
    /**
     * Returns integer value between 0 to 5, based on book condition
     * referred from `BookCondition` Enum type.
     *
     * @param bookCondition `BookCondition` enum type describing used book.
     * @return integer value describing rating for given used book.
     * -1, if condition not identified.
     */
    fun bookConditionRating(bookCondition: BookCondition?): Int {
        val ratingScale = BookCondition.values().size
        return when (bookCondition) {
            BookCondition.NEW -> 6 * 5 / ratingScale
            BookCondition.FINE -> 5 * 5 / ratingScale
            BookCondition.GOOD -> 4 * 5 / ratingScale
            BookCondition.LOOSE_BINDING -> 3 * 5 / ratingScale
            BookCondition.FAIR -> 2 * 5 / ratingScale
            BookCondition.POOR -> 5 / ratingScale
            else -> {
                -1
            }
        }
        return -1
    }

    /**
     * Chooses the url for image.
     * It defaults to choosing a medium size thumbnail image.
     * But does fallback for other options, if available.
     *
     * @param bookImageTypeStringHashMap map denoting book image size type to image url.
     * @return String url for the best chosen image size. Empty string if no link is present.
     */
    @JvmStatic
    fun preferredImageLink(bookImageTypeStringHashMap: HashMap<BookImageType, String?>): String? {
        var imageUrl = ""
        for ((key, value) in bookImageTypeStringHashMap) {
            if (key == BookImageType.THUMBNAIL) {
                if (value != null) {
                    imageUrl = value
                }
                break
            } else if (value != null && value.length != 0) {
                imageUrl = value
                break
            }
        }
        return imageUrl
    }

    /**
     * Given a list of searched books, this method validates entry for each
     * searched book and only returns the unique ones.
     *
     * @param searchedBookArrayList the array list containing searched books to be sterilized.
     * @return the array list containing sterilized searched book entries.
     */
    fun sterilizeSearchedBooks(searchedBookArrayList: ArrayList<SearchedBook?>?): ArrayList<SearchedBook?>? {
        /** Use a hash set to have unique values  */
        val uniqueSearchedBooks: MutableSet<SearchedBook> = HashSet()
        if (searchedBookArrayList != null) {
            for (searchedBook in searchedBookArrayList) {
                if (performValidationOnSearchedBook(searchedBook)) {
                    if (searchedBook != null) {
                        uniqueSearchedBooks.add(searchedBook)
                    }
                }
            }
        }
        return ArrayList(uniqueSearchedBooks)
    }

    /**
     * A Searched Book entry must have atleast below mentioned entries :
     * Title, Authors and IndustryIdentifier.
     *
     * This method validates these entries.
     *
     * @param searchedBook the book to be validated
     * @return boolean value denoting validation's success.
     */
    private fun performValidationOnSearchedBook(searchedBook: SearchedBook?): Boolean {
        if (searchedBook == null) {
            return false
        }
        if (searchedBook.title == null
            || searchedBook.title.length == 0
        ) {
            return false
        }
        if (searchedBook.authors == null || searchedBook.authors.size == 0 || searchedBook.authors[0] == null || searchedBook.authors[0]!!.length == 0) {
            return false
        }
        return if (searchedBook.industryIdentifier == null
            || searchedBook.industryIdentifier.length == 0
        ) {
            false
        } else true
    }

    /**
     * Converts bitmap image to byte array
     *
     * @param bitmap image
     * @return byte array got from image bitmap
     */
    fun convertBitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    /**
     * Converts byte array to image bitmap
     *
     * @param bytes byte array
     * @return bitmap image
     */
    @JvmStatic
    fun convertByteArrayToBitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes == null || bytes.size == 0) {
            null
        } else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * Static Map used to map each book condition with a corresponding string value.
     */
    private val BOOK_CONDITION_MAP =
        Collections.unmodifiableMap(object : HashMap<String?, BookCondition?>() {
            init {
                put("POOR", BookCondition.POOR)
                put("LOOSE_BINDING", BookCondition.LOOSE_BINDING)
                put("BINDING", BookCondition.BINDING_COPY)
                put("FAIR", BookCondition.FAIR)
                put("FINE", BookCondition.FINE)
                put("GOOD", BookCondition.GOOD)
                put("NEW", BookCondition.NEW)
            }
        })

    /**
     * This method returns corresponding string value for each book condition entry.
     * It uses a static unmodifiable hash map.
     *
     * @param bookCondition [BookCondition] describes the condition of uploaded book.
     * @return String value for the corresponding book condition.
     */
    @JvmStatic
    fun getStringForBookConditionEnum(bookCondition: BookCondition): String? {
        for ((key, value) in BOOK_CONDITION_MAP) {
            if (value == bookCondition) {
                return key
            }
        }
        return null
    }

    /**
     * This method returns the corresponding book condition for the string specified.
     * It uses a static unmodifiable hash map.
     *
     * @param condition string value denoting the book condition
     * @return [BookCondition] describes the condition of the uploaded book
     */
    @JvmStatic
    fun getBookConditionEnumFromString(condition: String): BookCondition? {
        for ((key, value) in BOOK_CONDITION_MAP) {
            if (key == condition) {
                return value
            }
        }
        return null
    }
}