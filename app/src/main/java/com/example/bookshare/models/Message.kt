package com.example.bookshare.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class Message : Parcelable {
    val phoneNumber: String?
    val message: String?
    val timeForMessage: String?
    val isMessageRead: Boolean

    private constructor(
        phoneNumber: String?,
        message: String?,
        timeForMessage: String?,
        messageRead: Boolean
    ) {
        this.phoneNumber = phoneNumber
        this.message = message
        this.timeForMessage = timeForMessage
        isMessageRead = messageRead
    }

    class MessageBuilder {
        private var phoneNumber: String? = null
        private var message: String? = null
        private var timeForMessage: String? = null
        private var messageRead = false
        fun addPhoneNumber(phoneNumber: String?): MessageBuilder {
            this.phoneNumber = phoneNumber
            return this
        }

        fun addMessage(message: String?): MessageBuilder {
            this.message = message
            return this
        }

        fun addTimeForMessage(timeForMessage: String?): MessageBuilder {
            this.timeForMessage = timeForMessage
            return this
        }

        fun addMessageRead(messageRead: Boolean): MessageBuilder {
            this.messageRead = messageRead
            return this
        }

        fun build(): Message {
            return Message(phoneNumber, message, timeForMessage, messageRead)
        }
    }
    /** */
    /****** Below methods make this class Parcelable  */
    /** */
    protected constructor(`in`: Parcel) {
        phoneNumber = `in`.readString()
        message = `in`.readString()
        timeForMessage = `in`.readString()
        isMessageRead = `in`.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(phoneNumber)
        dest.writeString(message)
        dest.writeString(timeForMessage)
        dest.writeByte((if (isMessageRead) 1 else 0).toByte())
    }

    companion object {
        @JvmField
        val CREATOR: Creator<Message> = object : Creator<Message> {
            override fun createFromParcel(`in`: Parcel): Message {
                return Message(`in`)
            }

            override fun newArray(size: Int): Array<Message?> {
                return arrayOfNulls(size)
            }
        }
    }
}