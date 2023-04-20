package com.example.bookshare.preferences

import android.content.Context
import com.example.bookshare.util.Constants
import org.json.JSONObject

class SharedPreferencesMap(context: Context) {
    private val context: Context

    init {
        this.context = context.applicationContext
    }

    fun saveAcraMap(inputMap: Map<String?, String?>?) {
        val pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val jsonObject = JSONObject(inputMap)
            val jsonString = jsonObject.toString()
            val editor = pSharedPref.edit()
            if (editor != null) {
                editor.remove(Constants.acraMap).apply()
                editor.putString(Constants.acraMap, jsonString)
                editor.apply()
            }
        }
    }

    fun loadAcraMap(): Map<String, String>? {
        val outputMap: MutableMap<String, String> = HashMap()
        val pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE)
        try {
            if (pSharedPref != null) {
                val jsonString = pSharedPref.getString(Constants.acraMap, JSONObject().toString())
                if (jsonString != null && jsonString.length > 0) {
                    val jsonObject = JSONObject(jsonString)
                    val keysItr = jsonObject.keys()
                    while (keysItr.hasNext()) {
                        val key = keysItr.next()
                        val value = jsonObject[key] as String
                        outputMap[key] = value
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return outputMap
    }

    fun removeAcraMap() {
        val pSharedPref = context.getSharedPreferences(Constants.acraPrefs, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val editor = pSharedPref.edit()
            if (editor != null) {
                editor.remove(Constants.acraMap)
                editor.apply()
            }
        }
    }

    fun saveFeedback(feedback: String?) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            if (editor != null) {
                editor.remove(Constants.feedbackMap).apply()
                editor.putString(Constants.feedbackMap, feedback)
                editor.apply()
            }
        }
    }

    fun loadFeedback(): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(Constants.feedbackMap, null)
    }

    fun removeFeedback() {
        val sharedPreferences =
            context.getSharedPreferences(Constants.feedbackPrefs, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            if (editor != null) {
                editor.remove(Constants.feedbackMap)
                editor.apply()
            }
        }
    }

    fun saveAgentName(name: String?) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.agentPrefs, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            if (editor != null) {
                editor.remove(Constants.agentName).apply()
                editor.putString(Constants.agentName, name)
                editor.apply()
            }
        }
    }

    fun loadAgentName(): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.agentPrefs, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(Constants.agentName, null)
    }

    fun savePassword(name: String?) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.passwordPrefs, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            if (editor != null) {
                editor.remove(Constants.passwordName).apply()
                editor.putString(Constants.passwordName, name)
                editor.apply()
            }
        }
    }

    fun loadPassword(): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.passwordPrefs, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(Constants.passwordName, null)
    }

    fun saveEmail(name: String?) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.emailPrefs, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            if (editor != null) {
                editor.remove(Constants.emailName).apply()
                editor.putString(Constants.emailName, name)
                editor.apply()
            }
        }
    }

    fun loadEmail(): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.emailPrefs, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(Constants.emailName, null)
    }

    fun saveUid(name: String?) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.uidPrefs, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            if (editor != null) {
                editor.remove(Constants.uidName).apply()
                editor.putString(Constants.uidName, name)
                editor.apply()
            }
        }
    }

    fun loadUid(): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.uidPrefs, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(Constants.uidName, null)
    }

    fun savePhoneNumber(name: String?) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.phoneNumberPrefs, Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            if (editor != null) {
                editor.remove(Constants.phoneNumberName).apply()
                editor.putString(Constants.phoneNumberName, name)
                editor.apply()
            }
        }
    }

    fun loadPhoneNumber(): String? {
        val sharedPreferences =
            context.getSharedPreferences(Constants.phoneNumberPrefs, Context.MODE_PRIVATE)
        return sharedPreferences?.getString(Constants.phoneNumberName, null)
    }
}