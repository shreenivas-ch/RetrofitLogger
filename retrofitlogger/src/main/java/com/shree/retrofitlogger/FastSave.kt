package com.shree.retrofitlogger

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.ArrayList

class FastSave private constructor() {

    fun saveString(key: String, value: String) {
        val editor = mSharedPreferences!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String?): String? {
        return if (isKeyExists(key)) {
            mSharedPreferences!!.getString(key, defaultValue)
        } else defaultValue
    }

    fun clearSession() {
        val editor = mSharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }

    fun deleteValue(key: String): Boolean {
        if (isKeyExists(key)) {
            val editor = mSharedPreferences!!.edit()
            editor.remove(key)
            editor.apply()
            return true
        }

        return false
    }

    fun isKeyExists(key: String): Boolean {
        val map = mSharedPreferences!!.all
        if (map.containsKey(key)) {
            return true
        } else {
            Log.e("FastSave", "No element founded in sharedPrefs with the key $key")
            return false
        }
    }

    companion object {

        private var instance: FastSave? = null
        private var mSharedPreferences: SharedPreferences? = null

        fun init(context: Context) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun getInstance(): FastSave {
            if (instance == null) {
                validateInitialization()
                synchronized(FastSave::class.java) {
                    if (instance == null) {
                        instance = FastSave()
                    }
                }
            }
            return instance!!
        }


        private fun validateInitialization() {
            if (mSharedPreferences == null)
                throw FastException("FastSave Library must be initialized inside your application class by calling FastSave.init(getApplicationContext)")
        }
    }

}
