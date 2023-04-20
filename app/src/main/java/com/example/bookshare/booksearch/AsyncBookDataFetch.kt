package com.example.bookshare.booksearch

import android.os.AsyncTask
import com.example.bookshare.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AsyncBookDataFetch : AsyncTask<String?, Void?, String>() {
    private var taskCompleteEvent: TaskCompleteEvent? = null
    fun setTaskCompleteEvent(taskCompleteEvent: TaskCompleteEvent?) {
        this.taskCompleteEvent = taskCompleteEvent
    }

    /** Callback for Async Task Completion  */
    interface TaskCompleteEvent {
        fun onTaskComplete(result: String?)
        fun onTaskFailed()
    }

    protected override fun doInBackground(vararg params: String?): String? {
        /** URL must not be null or empty  */
        if (params == null || params.isEmpty()) {
            /** empty url, handle gracefully  */
            return ""
        }
        /** Only Take first url  */
        val url = params[0]
        return try {
            /** Try make a connection and return response  */
            makeConnectionAndFetchResult(url!!)
        } catch (e: Exception) {
            /** Could not fetch data, handle gracefully  */
            ""
        }
    }

    override fun onPostExecute(result: String) {
        /** Nothing returned, task failed  */
        if (result == "") {
            if (taskCompleteEvent != null) {
                taskCompleteEvent!!.onTaskFailed()
            }
            return
        }
        /** Call back for task completion event  */
        if (taskCompleteEvent != null) {
            taskCompleteEvent!!.onTaskComplete(result)
        }
    }

    /** Handles connection and fetches response  */
    @Throws(Exception::class)
    private fun makeConnectionAndFetchResult(url: String): String {
        /** Clean up ensures cleaning up of resources  */
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.connectTimeout = CONNECTION_TIMEOUT
        urlConnection.readTimeout = CONNECTION_TIMEOUT
        /** Need to set api key  */
        urlConnection.setRequestProperty("key", BuildConfig.GOOGLE_BOOK_KEY)
        /** Check for a successful connection  */
        if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) {
            /** Connection failed, handle gracefully  */
            return ""
        }
        /** Convert read stream to string and return it  */
        val inputStream = urlConnection.inputStream
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var inputStr: String?
        val responseStrBuilder = StringBuilder()
        while (bufferedReader.readLine().also { inputStr = it } != null) {
            responseStrBuilder.append(inputStr)
        }
        return responseStrBuilder.toString()
    }

    companion object {
        /** Time limit  */
        private const val CONNECTION_TIMEOUT = 10000
    }
}