package com.example.bookshare.util

import android.os.Bundle
import android.os.Parcelable
import com.example.bookshare.models.SearchedBook
import com.example.bookshare.models.UploadedBook
import com.example.bookshare.models.UserData

object BundleUtil {
    /** Returns string value from initialized bundle  */
    fun getStringFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: String?
    ): String? {
        var returnValue: String? = null
        if (intentExtras != null) {
            returnValue = intentExtras.getString(token)
        }
        if (returnValue != null) {
            return returnValue
        }
        if (savedInstanceState != null) {
            returnValue = savedInstanceState.getSerializable(token) as String?
        }
        if (returnValue != null) {
            return returnValue
        }
        returnValue = defaultValue
        return returnValue
    }

    /** Returns string value from initialized bundle  */
    fun getIntFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: Int
    ): Int {
        var returnValue: Int? = null
        if (intentExtras != null) {
            returnValue = intentExtras.getInt(token)
        }
        if (returnValue != null) {
            return returnValue
        }
        if (savedInstanceState != null) {
            returnValue = savedInstanceState.getSerializable(token) as Int?
        }
        if (returnValue != null) {
            return returnValue
        }
        returnValue = defaultValue
        return returnValue
    }

    /** Returns boolean value from initialized bundle  */
    fun getBooleanFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: Boolean
    ): Boolean {
        var returnValue: Boolean? = null
        if (intentExtras != null) {
            returnValue = intentExtras.getBoolean(token)
        }
        if (returnValue != null) {
            return returnValue
        }
        if (savedInstanceState != null) {
            returnValue = savedInstanceState.getSerializable(token) as Boolean
        }
        if (returnValue != null) {
            return returnValue
        }
        returnValue = defaultValue
        return returnValue
    }

    /** Returns int value from initialized bundle  */
    fun getLongFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: Long?
    ): Long? {
        var returnValue: Long? = null
        if (intentExtras != null) {
            returnValue = intentExtras.getLong(token)
        }
        if (returnValue != null) {
            return returnValue
        }
        if (savedInstanceState != null) {
            returnValue = savedInstanceState.getSerializable(token) as Long?
        }
        if (returnValue != null) {
            return returnValue
        }
        returnValue = defaultValue
        return returnValue
    }

    /** Get UserData model object from bundle instance  */
    fun getUserDataFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: UserData?
    ): UserData? {
        var userData: UserData? = null
        if (savedInstanceState != null) {
            userData = savedInstanceState.getParcelable<Parcelable>(token) as UserData?
        }
        if (userData != null) {
            return userData
        }
        if (intentExtras != null) {
            userData = intentExtras.getParcelable<Parcelable>(token) as UserData?
        }
        if (userData != null) {
            return userData
        }
        userData = defaultValue
        return userData
    }

    /** Get Searched Book From Bundle object  */
    fun getSearchedBookFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: SearchedBook?
    ): SearchedBook? {
        var searchedBook: SearchedBook? = null
        if (savedInstanceState != null) {
            searchedBook = savedInstanceState.getParcelable<Parcelable>(token) as SearchedBook?
        }
        if (searchedBook != null) {
            return searchedBook
        }
        if (intentExtras != null) {
            searchedBook = intentExtras.getParcelable<Parcelable>(token) as SearchedBook?
        }
        if (searchedBook != null) {
            return searchedBook
        }
        searchedBook = defaultValue
        return searchedBook
    }

    /** Get Uploaded Book from Bundle object  */
    fun getUploadedBookFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: UploadedBook?
    ): UploadedBook? {
        var uploadedBook: UploadedBook? = null
        if (savedInstanceState != null) {
            uploadedBook = savedInstanceState.getParcelable<Parcelable>(token) as UploadedBook?
        }
        if (uploadedBook != null) {
            return uploadedBook
        }
        if (intentExtras != null) {
            uploadedBook = intentExtras.getParcelable<Parcelable>(token) as UploadedBook?
        }
        if (uploadedBook != null) {
            return uploadedBook
        }
        uploadedBook = defaultValue
        return uploadedBook
    }

    /** Get byte array from bundle  */
    fun getByteArrayFromBundle(
        savedInstanceState: Bundle?,
        intentExtras: Bundle?,
        token: String?,
        defaultValue: ByteArray?
    ): ByteArray? {
        var returnValue: ByteArray? = null
        if (intentExtras != null) {
            returnValue = intentExtras.getByteArray(token)
        }
        if (returnValue != null) {
            return returnValue
        }
        if (savedInstanceState != null) {
            returnValue = savedInstanceState.getByteArray(token)
        }
        if (returnValue != null) {
            return returnValue
        }
        returnValue = defaultValue
        return returnValue
    }
}