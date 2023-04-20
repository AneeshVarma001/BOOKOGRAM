package com.example.bookshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshare.adapter.BaseAdapter.DataObjectHolder

abstract class BaseAdapter<T>(
    private val childLayoutResId: Int,
    private var mDataset: ArrayList<T>,
    var context: Context,
    footerAdded: Boolean,
    footerLayoutResId: Int
) : RecyclerView.Adapter<DataObjectHolder>() {
    private val footerLayoutResId: Int

    init {
        isFooterAdded = footerAdded
        this.footerLayoutResId = footerLayoutResId
    }

    /** Static view holder for Adapter  */
    abstract class DataObjectHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    ), View.OnClickListener {
        /** Constructor with root view  */
        init {
            initialize(itemView)
            itemView?.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (recyclerViewAdapterEvents != null) {
                recyclerViewAdapterEvents!!.onItemClick(
                    position, v
                )
            }
        }

        /** Needs to be implemented by implementing class for initializing view elements in each item's view  */
        abstract fun initialize(rootView: View?)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && isFooterAdded) {
            TYPE_FOOTER
        } else super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataObjectHolder {
        return if (viewType == TYPE_FOOTER) {
            onCreateFooterInitializeDataObjectHolder(
                LayoutInflater.from(parent.context).inflate(footerLayoutResId, parent, false)
            )!! } else {
            onCreateInitializeDataObjectHolder(
                LayoutInflater.from(parent.context).inflate(
                    childLayoutResId, parent, false
                )
            )!!
        }
    }

    /** Initialize here with the implementation for DataObjectHolder class.  */
    abstract fun onCreateInitializeDataObjectHolder(v: View?): DataObjectHolder?

    /** Initialize footer here with the implementation for DataObjectHolder class.  */
    abstract fun onCreateFooterInitializeDataObjectHolder(v: View?): DataObjectHolder?
    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
        if (holder.itemViewType == TYPE_FOOTER) {
            onBindFooterItemViewHolder(holder)
        } else {
            onBindItemViewHolder(holder, position, mDataset!![position])
        }
    }

    /** on Bind View holder for sub-class  */
    abstract fun onBindItemViewHolder(holder: DataObjectHolder?, position: Int, dataObj: T)

    /** on Bind View holder for last item, called when there is a footer view added to it  */
    abstract fun onBindFooterItemViewHolder(holder: DataObjectHolder?)
    override fun getItemCount(): Int {
        return if (!isFooterAdded) {
            if (mDataset != null) mDataset!!.size else 0
        } else {
            if (mDataset != null) mDataset!!.size + 1 else 1
        }
    }

    /** Add single item at the end  */
    fun addItem(dataObj: T) {
        if (mDataset != null) {
            mDataset!!.add(dataObj)
            notifyDataSetChanged()
        }
    }

    /** Add Multiple items at the end  */
    open fun addMultipleItems(objectArrayList: ArrayList<T>?) {
        if (mDataset != null) {
            mDataset!!.addAll(objectArrayList!!)
            notifyDataSetChanged()
        }
    }

    /** Delete Single Item  */
    fun deleteItem(dataObj: T) {
        if (mDataset != null) {
            mDataset!!.removeAt(mDataset!!.indexOf(dataObj))
            notifyDataSetChanged()
        }
    }

    /** Deletes all items  */
    fun deleteAllItems() {
        if (mDataset != null) {
            mDataset = ArrayList()
            notifyDataSetChanged()
        }
    }

    fun getItemAtPosition(position: Int): T? {
        return if (mDataset != null) {
            mDataset!![position]
        } else null
    }

    fun changeDataCompletely(tArrayList: ArrayList<T>?) {
        mDataset!!.clear()
        mDataset = tArrayList?.let { ArrayList(it) }?:run{ArrayList()}
        notifyDataSetChanged()
    }

    fun setRecyclerViewAdapterEvents(recyclerViewAdapterEvents: RecyclerViewAdapterEvents?) {
        Companion.recyclerViewAdapterEvents = recyclerViewAdapterEvents
    }

    /** Callback events  */
    interface RecyclerViewAdapterEvents {
        fun onItemClick(position: Int, v: View?)
    }

    companion object {
        private var recyclerViewAdapterEvents: RecyclerViewAdapterEvents? = null
        private const val TYPE_FOOTER = Int.MIN_VALUE + 1

        /** Reflects addition of a footer view  */
        private var isFooterAdded = false
//            private get() = Companion.field
    }
}