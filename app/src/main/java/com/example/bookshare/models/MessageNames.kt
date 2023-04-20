package com.example.bookshare.models

import android.graphics.Bitmap

class MessageNames private constructor(
    val bitmap: Bitmap?,
    val email: String?,
    val name: String?
) {

    class MessageNamesBuilder {
        private var bitmap: Bitmap? = null
        private var name: String? = null
        private var email: String? = null
        fun addEmail(email: String?): MessageNamesBuilder {
            this.email = email
            return this
        }

        fun addName(name: String?): MessageNamesBuilder {
            this.name = name
            return this
        }

        fun addBitmap(bitmap: Bitmap?): MessageNamesBuilder {
            this.bitmap = bitmap
            return this
        }

        fun build(): MessageNames {
            return MessageNames(bitmap, email, name)
        }
    }
}