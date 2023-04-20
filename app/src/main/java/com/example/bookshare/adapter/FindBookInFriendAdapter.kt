package com.example.bookshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.bookshare.R
import com.example.bookshare.alertDialogs.AlertMessageDialog
import com.example.bookshare.alertDialogs.AlertMessageDialog.OnAlertButtonClicked
import com.example.bookshare.firebase.MessagesFirebaseHelper
import com.example.bookshare.models.FindBookMetaData

class FindBookInFriendAdapter(
    private val context: Context,
    private var findBookInFriendArrayList: ArrayList<FindBookMetaData>,
    private val inflateLayoutId: Int,
    lookupBook: String
) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private val lookupBook: String

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.lookupBook = lookupBook
    }

    override fun getCount(): Int {
        return findBookInFriendArrayList.size
    }

    override fun getItem(position: Int): Any {
        return findBookInFriendArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /** Holder class for this adapter  */
    internal inner class FindBookInFriendAdapterHolder {
        var nameTextView: TextView? = null
        var bookTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        /** Get the convertView  */
        var view = convertView
        /** Holder instance  */
        val messagesNamesAdapterHolder: FindBookInFriendAdapterHolder
        /** if convertView is null, its first inflation for list item view  */
        if (view == null) {
            view = layoutInflater.inflate(inflateLayoutId, parent, false)
            messagesNamesAdapterHolder = FindBookInFriendAdapterHolder()
            messagesNamesAdapterHolder.bookTextView =
                view.findViewById<View>(R.id.has_book_text_view) as TextView
            messagesNamesAdapterHolder.nameTextView =
                view.findViewById<View>(R.id.contact_name_text_view) as TextView
            view.tag = messagesNamesAdapterHolder
        } else {
            /** else, get the holder from the view's tag  */
            messagesNamesAdapterHolder = view.tag as FindBookInFriendAdapterHolder
        }
        val name = findBookInFriendArrayList[position].name
        if (name == null
            || name.length == 0
        ) {
            messagesNamesAdapterHolder.nameTextView!!.text =
                findBookInFriendArrayList[position].phoneNumber
        } else {
            messagesNamesAdapterHolder.nameTextView!!.text = name
        }
        messagesNamesAdapterHolder.bookTextView!!.text =
            String.format(context.getString(R.string.has_book), lookupBook)
        view!!.setOnClickListener(FindBookInFriendItemClickListener(position))
        /** return the inflated view  */
        return view
    }

    fun updateAll(findBookMetaDataArrayList: ArrayList<FindBookMetaData>?) {
        findBookInFriendArrayList.clear()
        findBookInFriendArrayList = ArrayList(findBookMetaDataArrayList)
        notifyDataSetChanged()
    }

    internal inner class FindBookInFriendItemClickListener(private val position: Int) :
        View.OnClickListener {
        override fun onClick(v: View) {
            /** Ask user to send message to friend who has the book  */
            val alertMessageDialog =
                AlertMessageDialog(
                    context,
                    context.getString(R.string.lend_book),
                    context.getString(R.string.ask_your_friend_to_lend),
                    context.getString(R.string.no),
                    context.getString(R.string.yes)
                )
            alertMessageDialog.show()
            alertMessageDialog.setOnAlertButtonClicked(object : OnAlertButtonClicked {
                override fun onLeftButtonClicked(v: View?) {}
                override fun onRightButtonClicked(v: View?) {
                    val messagesFirebaseHelper = MessagesFirebaseHelper()
                    messagesFirebaseHelper.sendMessage(
                        findBookInFriendArrayList[position].phoneNumber,
                        context.getString(R.string.hey_i_need) + lookupBook + context.getString(R.string.book_book_share_tells_me)
                    )
                }
            })
        }
    }
}