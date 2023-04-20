package com.example.bookshare.util

import com.example.bookshare.models.Message
import java.util.*
import kotlin.collections.ArrayList

object MessageUtil {
    fun sortMessages(messageArrayList: ArrayList<Message?>) {
        Collections.sort(messageArrayList) { lhs, rhs -> -1 * lhs!!.timeForMessage!!.compareTo(rhs!!.timeForMessage!!) }
    }
}