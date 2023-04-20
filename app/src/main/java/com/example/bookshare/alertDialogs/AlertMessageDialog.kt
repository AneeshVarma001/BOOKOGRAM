package com.example.bookshare.alertDialogs

import android.app.Activity
import android.content.Context
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.bookshare.R

class AlertMessageDialog(
    context: Context,
    title: String?,
    body: Spanned?,
    leftButtonTitle: String?,
    rightButtonTitle: String?
) {
    private val alertDialog: AlertDialog?
    private var onAlertButtonClicked: OnAlertButtonClicked? = null

    constructor(
        context: Context,
        title: String?,
        body: String?,
        leftButtonTitle: String?,
        rightButtonTitle: String?
    ) : this(context, title, Html.fromHtml(body), leftButtonTitle, rightButtonTitle) {
    }

    init {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val rootView =
            (context as Activity).layoutInflater.inflate(R.layout.alert_dialog_layout, null)
        alertDialogBuilder.setView(rootView)
        alertDialog = alertDialogBuilder.create()
        /** Set Dialog Values  */
        (rootView.findViewById<View>(R.id.title) as TextView).text = title
        (rootView.findViewById<View>(R.id.body) as TextView).text = body
        (rootView.findViewById<View>(R.id.left_button) as TextView).text = leftButtonTitle
        (rootView.findViewById<View>(R.id.right_button) as TextView).text = rightButtonTitle
        /** On Click Listeners  */
        (rootView.findViewById<View>(R.id.left_button) as TextView).setOnClickListener { v ->
            if (onAlertButtonClicked != null) {
                onAlertButtonClicked!!.onLeftButtonClicked(v)
            }
            cancel()
        }
        (rootView.findViewById<View>(R.id.right_button) as TextView).setOnClickListener { v ->
            if (onAlertButtonClicked != null) {
                onAlertButtonClicked!!.onRightButtonClicked(v)
            }
            cancel()
        }
    }

    /** Shows the alert dialog  */
    fun show() {
        alertDialog?.show()
    }

    /** Cancels the alert dialog  */
    fun cancel() {
        alertDialog?.cancel()
    }

    /** Set alert button clicked  */
    fun setOnAlertButtonClicked(onAlertButtonClicked: OnAlertButtonClicked?) {
        this.onAlertButtonClicked = onAlertButtonClicked
    }

    /** Callback for alert buttons clicked  */
    interface OnAlertButtonClicked {
        fun onLeftButtonClicked(v: View?)
        fun onRightButtonClicked(v: View?)
    }
}