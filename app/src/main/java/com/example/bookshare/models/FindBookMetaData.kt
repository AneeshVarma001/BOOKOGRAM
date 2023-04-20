package com.example.bookshare.models

class FindBookMetaData  constructor(
    val name: String?,
    val phoneNumber: String?,
    val bId: String?
) {

    class FindBookMetaDataBuilder {
        private var name: String? = null
        private var phoneNumber: String? = null
        private var bId: String? = null
        fun addName(name: String?): FindBookMetaDataBuilder {
            this.name = name
            return this
        }

        fun addPhoneNumber(phoneNumber: String?): FindBookMetaDataBuilder {
            this.phoneNumber = phoneNumber
            return this
        }

        fun addBId(bId: String?): FindBookMetaDataBuilder {
            this.bId = bId
            return this
        }

        fun build(): FindBookMetaData {
            return FindBookMetaData(name, phoneNumber, bId)
        }
    }
}