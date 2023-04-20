package com.example.bookshare.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.bookshare.R
import com.example.bookshare.util.Constants

class MainScreenActivity : BaseAbstractActivity() {
    /**
     * Called when the activity is starting.
     *
     *
     * This activity extends [BaseAbstractActivity], an abstract activity,
     * which mainly handles permissions grant and navigation drawer related events.
     *
     *
     * Need to call [BaseAbstractActivity.setContentViewAndId], instead
     * of [androidx.appcompat.app.AppCompatActivity.setContentView], because
     * the abstract activity handles all layout inflation by itself, when setContentViewAndId
     * is called with a constant Activity Id for each Activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in [.onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** Full Screen flag : this draws behind status bar. Our status bar is translucent  */
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        /** Set activity content view by calling base activity method  */
        super.setContentViewAndId(R.layout.activity_main_screen, Constants.homeActivity)
        /** Capture click event for find books  */
        findViewById<View>(R.id.find_books_colored_circular_view_with_shadow).setOnClickListener { v ->
            findBooksClicked(
                v
            )
        }
        /** Capture click event for upload books  */
        findViewById<View>(R.id.upload_books_colored_circular_view_with_shadow).setOnClickListener { v ->
            uploadBooksClicked(
                v
            )
        }
        /** Set up name in drawer   */
//        (findViewById<View>(R.id.agent_name_text_view) as TextView).text =
//            BookShareApplication.getInstance().agentName
    }

    /** Find books clicked  */
    private fun findBooksClicked(v: View?) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            /** Start the search book activity  */
            /** Start the search book activity  */
            /** Start the search book activity  */
            /** Start the search book activity  */
            val intent = Intent(this@MainScreenActivity, SearchBookActivity::class.java)
            startActivity(intent)
        }, 500)
    }

    /** Upload books clicked  */
    private fun uploadBooksClicked(v: View?) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            /** Shared Element Activity Transition  */
            /** Shared Element Activity Transition  */
            val intent = Intent(this@MainScreenActivity, UploadBookActivity::class.java)
            val pair1 = Pair.create(
                findViewById<View>(R.id.upload_books_colored_circular_view),
                getString(R.string.upper_half)
            )
            val pair2 = Pair.create(
                findViewById<View>(R.id.upload_books_white_circular_view),
                getString(R.string.lower_half)
            )
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainScreenActivity,
                pair1,
                pair2
            )
            startActivity(intent, options.toBundle())
        }, 500)
    }
}