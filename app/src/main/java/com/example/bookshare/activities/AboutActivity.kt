package com.example.bookshare.activities

import android.os.Bundle
import com.example.bookshare.R
import com.example.bookshare.util.Constants

/**
 * Activity for describing about app.
 */
class AboutActivity : BaseAbstractActivity() {
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
        /**
         * Set activity content view by calling base activity method
         */
        super.setContentViewAndId(R.layout.activity_about, Constants.aboutActivity)
    }
}