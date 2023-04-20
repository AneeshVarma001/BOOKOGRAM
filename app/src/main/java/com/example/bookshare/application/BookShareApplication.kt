package com.example.bookshare.application

import android.app.Application
import com.example.bookshare.preferences.SharedPreferencesMap
import com.firebase.client.Firebase

class BookShareApplication : Application() {
    private var agentName: String? = null
    private var emailStr: String? = null
    private var passwordStr: String? = null
    private val phoneNumber: String? = null
    private var uId: String? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        /** Initialize firebase  */
        Firebase.setAndroidContext(this)
    }


    /**************************************************************************************
     * Agent Name ****************************************
     */
    /* */
    /** Get the agent name  */
    fun getAgentName(): String? {
        if (agentName == null || agentName!!.length == 0) {
            val sharedPreferencesMap = SharedPreferencesMap(this)
            this.agentName = sharedPreferencesMap.loadAgentName()
        }
        return agentName
    }

    /** Sets the agent name  */
    fun setAgentName(agentName: String?) {
        this.agentName = agentName

        /**  * Save agent name permanently  **/
        val sharedPreferencesMap = SharedPreferencesMap(this)
        sharedPreferencesMap.saveAgentName(agentName)
    }
    /**************************************************************************************
     * Email  ********************************************
     */
    /** Get the email  */
    fun getEmail(): String? {
        if (emailStr == null || emailStr!!.length == 0) {
            val sharedPreferencesMap = SharedPreferencesMap(this)
            emailStr = sharedPreferencesMap.loadEmail()
        }
        return emailStr
    }

    /** Sets the email  */
    fun setEmail(email: String?) {
        this.emailStr = email
        /** Save email permanently  */
        val sharedPreferencesMap = SharedPreferencesMap(this)
        sharedPreferencesMap.saveEmail(email)
    }
    /**************************************************************************************
     * Password ******************************************
     */
    /** Get the password  */
    fun getPassword(): String? {
        if (passwordStr == null || passwordStr!!.isEmpty()) {
            val sharedPreferencesMap = SharedPreferencesMap(this)
            passwordStr = sharedPreferencesMap.loadPassword()
        }
        return passwordStr
    }

    /** Sets the password  */
    fun setPassword(password: String?) {
        this.passwordStr = password
        /** Save password permanently  */
        val sharedPreferencesMap = SharedPreferencesMap(this)
        sharedPreferencesMap.savePassword(password)
    }
    /**************************************************************************************
     * Uid ***********************************************
     */
    /** Get the uId  */
    /** Save uId name permanently  */
    /** Sets the uId  */
    var uid: String?
        get() {
            if (uId == null || uId!!.length == 0) {
                val sharedPreferencesMap = SharedPreferencesMap(this)
                uId = sharedPreferencesMap.loadUid()
            }
            return uId
        }
        set(uId) {
            this.uId = uId
            /** Save uId name permanently  */
            val sharedPreferencesMap = SharedPreferencesMap(this)
            sharedPreferencesMap.saveUid(uId)
        }

    companion object {
        /** Get the application instance  */
        @get:Synchronized
        var instance: BookShareApplication? = null
            private set

    }
}