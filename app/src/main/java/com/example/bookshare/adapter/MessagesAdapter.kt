package com.example.bookshare.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.bookshare.R
import com.example.bookshare.application.BookShareApplication
import com.example.bookshare.models.Message
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MessagesAdapter(
    childLayoutResId: Int,
    mDataset: ArrayList<Message?>,
     val ctx: Context,
    private val name:String
) : BaseAdapter<Message?>(childLayoutResId, mDataset, ctx, false, -1) {
    override fun onCreateInitializeDataObjectHolder(v: View?): DataObjectHolder {
        return MessageDataObjectHolder(v)
    }

    override fun onCreateFooterInitializeDataObjectHolder(v: View?): DataObjectHolder? {
        return null
    }

    override fun onBindItemViewHolder(holder: DataObjectHolder?, position: Int, dataObj: Message?) {
        val messageDataObjectHolder = holder as MessageDataObjectHolder?
        messageDataObjectHolder!!.messageBody!!.text = dataObj!!.message
        messageDataObjectHolder.messageTime!!.text =
            Date(dataObj.timeForMessage!!.toLong()).toString()
        val currentPhoneNumber: String = BookShareApplication.instance!!.getEmail()!!.replace("[.]".toRegex(),"_dot_").replace("@".toRegex(),"_at_the_rate_")
        if (currentPhoneNumber == dataObj.phoneNumber
        ) {
            messageDataObjectHolder.messageSender!!.visibility = View.GONE
            messageDataObjectHolder.leftFab!!.visibility = View.GONE
            messageDataObjectHolder.rightFab!!.visibility = View.VISIBLE
            messageDataObjectHolder.messageReceiver!!.visibility = View.VISIBLE
        } else {
            messageDataObjectHolder.rightFab!!.visibility = View.GONE
            messageDataObjectHolder.messageReceiver!!.visibility = View.GONE
            messageDataObjectHolder.leftFab!!.visibility = View.VISIBLE
            messageDataObjectHolder.messageSender!!.visibility = View.VISIBLE
            messageDataObjectHolder.messageSender!!.text = name
        }
    }

    override fun onBindFooterItemViewHolder(holder: DataObjectHolder?) {}

    /**
     * Static View Holder Class
     */
    class MessageDataObjectHolder
    /**
     * Constructor with root view
     *
     * @param itemView root view
     */
        (itemView: View?) : DataObjectHolder(itemView) {
        var messageSender: TextView? = null
        var messageBody: TextView? = null
        var messageTime: TextView? = null
        var messageReceiver: TextView? = null
        var leftFab: FloatingActionButton? = null
        var rightFab: FloatingActionButton? = null
        override fun initialize(rootView: View?) {
            messageSender =
                rootView!!.findViewById<View>(R.id.message_sender_name_text_view) as TextView
            messageBody = rootView.findViewById<View>(R.id.message_body_text_view) as TextView
            messageTime = rootView.findViewById<View>(R.id.time_for_message_text_view) as TextView
            messageReceiver =
                rootView.findViewById<View>(R.id.message_receiver_name_text_view) as TextView
            leftFab = rootView.findViewById<View>(R.id.left_fab) as FloatingActionButton
            rightFab = rootView.findViewById<View>(R.id.right_fab) as FloatingActionButton
        }
    }
}