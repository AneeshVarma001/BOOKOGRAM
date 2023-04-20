package com.example.bookshare.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class UserData : Parcelable {
    /** Name of the user who wants to login  */
    var userName: String?

    /** Email Id of the user who wants to login  */
    var emailId: String?

    /** Phone Number of the user who wants to login  */
    var phoneNumber: String?

    constructor(userName: String?, emailId: String?, phoneNumber: String?) {
        this.userName = userName
        this.emailId = emailId
        this.phoneNumber = phoneNumber
    }
    /** */
    /****** Below methods make this class Parcelable  */
    /** */
    protected constructor(`in`: Parcel) {
        userName = `in`.readString()
        emailId = `in`.readString()
        phoneNumber = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(userName)
        dest.writeString(emailId)
        dest.writeString(phoneNumber)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<UserData> = object : Creator<UserData> {
            override fun createFromParcel(`in`: Parcel): UserData? {
                return UserData(`in`)
            }

            override fun newArray(size: Int): Array<UserData?> {
                return arrayOfNulls(size)
            }
        }
    }
}