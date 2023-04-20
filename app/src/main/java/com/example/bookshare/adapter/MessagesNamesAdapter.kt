package com.example.bookshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.bookshare.R
import com.example.bookshare.models.MessageNames
import com.mikhaellopez.circularimageview.CircularImageView

class MessagesNamesAdapter(
    context: Context,
    private val messageNamesArrayList: ArrayList<MessageNames>,
    private val inflateLayoutId: Int,
    numberUnreadCountMap: HashMap<String, Int>?
) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private var numberUnreadCountMap: HashMap<String, Int>?
    private var messagesNamesItemClickEvent: MessagesNamesItemClickEvent? = null

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.numberUnreadCountMap = numberUnreadCountMap
    }

    fun updateNumberUnreadCountMap(numberUnreadCountMap: HashMap<String, Int>?) {
        this.numberUnreadCountMap = HashMap(numberUnreadCountMap)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return messageNamesArrayList.size
    }

    override fun getItem(position: Int): Any {
        return Any()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setMessagesNamesItemClickEvent(messagesNamesItemClickEvent: MessagesNamesItemClickEvent?) {
        this.messagesNamesItemClickEvent = messagesNamesItemClickEvent
    }

    /** Holder class for this adapter  */
    internal inner class MessagesNamesAdapterHolder {
        /**
         * The circular Image View for setting display icon
         */
        var circularImageView: CircularImageView? = null

        /**
         * Text View to show name of the contact
         */
        var nameTextView: TextView? = null

        /**
         * Text View to show email of the contact
         */
        var emailIdTextView: TextView? = null
        var messsageCounterRelativeLayout: RelativeLayout? = null
        var messageCounterTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        /** Get the convertView  */
        var view = convertView
        /** Holder instance  */
        val messagesNamesAdapterHolder: MessagesNamesAdapterHolder
        /** if convertView is null, its first inflation for list item view  */
        if (view == null) {
            view = layoutInflater.inflate(inflateLayoutId, parent, false)
            messagesNamesAdapterHolder = MessagesNamesAdapterHolder()
            messagesNamesAdapterHolder.circularImageView =
                view.findViewById<View>(R.id.contact_photo) as CircularImageView
            messagesNamesAdapterHolder.nameTextView =
                view.findViewById<View>(R.id.name_text_view) as TextView
            messagesNamesAdapterHolder.emailIdTextView =
                view.findViewById<View>(R.id.phone_number) as TextView
            messagesNamesAdapterHolder.messsageCounterRelativeLayout =
                view.findViewById<View>(R.id.messsage_counter_relative_layout_in_tile) as RelativeLayout
            messagesNamesAdapterHolder.messageCounterTextView =
                view.findViewById<View>(R.id.message_count_text_view_in_tile) as TextView
            view.tag = messagesNamesAdapterHolder
        } else {
            /** else, get the holder from the view's tag  */
            messagesNamesAdapterHolder = view.tag as MessagesNamesAdapterHolder
        }
        if (messageNamesArrayList[position].bitmap == null) {
            messagesNamesAdapterHolder.circularImageView!!.setImageResource(R.drawable.contact)
        } else {
            messagesNamesAdapterHolder.circularImageView!!.setImageBitmap(messageNamesArrayList[position].bitmap)
        }
        /** Set the email number  */
        messagesNamesAdapterHolder.emailIdTextView!!.text =
            messageNamesArrayList[position].email
        /** Set the name  */
        messagesNamesAdapterHolder.nameTextView!!.text = messageNamesArrayList[position].name
        /** Hides counter  */
        messagesNamesAdapterHolder.messsageCounterRelativeLayout!!.visibility = View.GONE
        /** Only makes counter visible if number of unread messages are greater than 0  */
        if (numberUnreadCountMap != null && numberUnreadCountMap!!.size != 0) {
            var emailId = messageNamesArrayList[position].email
            if (emailId!!.length > 10) {
                emailId = emailId.substring(3)
            }
            if (numberUnreadCountMap!!.keys.contains(emailId)) {
                val count = numberUnreadCountMap!![emailId]!!
                if (count != 0) {
                    messagesNamesAdapterHolder.messsageCounterRelativeLayout!!.visibility =
                        View.VISIBLE
                    messagesNamesAdapterHolder.messageCounterTextView!!.text = count.toString()
                }
            }
        }
        view!!.setOnClickListener(MessagesNamesItemClickListener(position))
        /** return the inflated view  */
        return view
    }

    interface MessagesNamesItemClickEvent {
        fun onItemClicked(v: View?, position: Int)
    }

    internal inner class MessagesNamesItemClickListener(private val position: Int) :
        View.OnClickListener {
        override fun onClick(v: View) {
            if (messagesNamesItemClickEvent != null) {
                messagesNamesItemClickEvent!!.onItemClicked(v, position)
            }
        }
    }
}