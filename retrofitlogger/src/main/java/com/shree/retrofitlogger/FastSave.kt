package com.shree.retrofitlogger

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.ArrayList

class FastSave private constructor() {

    fun saveInt(key: String, value: Int) {
        val editor = mSharedPreferences!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return if (isKeyExists(key)) {
            mSharedPreferences!!.getInt(key, defaultValue)
        } else defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        val editor = mSharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (isKeyExists(key)) {
            mSharedPreferences!!.getBoolean(key, defaultValue)
        } else defaultValue
    }


    fun saveFloat(key: String, value: Float) {
        val editor = mSharedPreferences!!.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        return if (isKeyExists(key)) {
            mSharedPreferences!!.getFloat(key, defaultValue)
        } else defaultValue
    }


    fun saveLong(key: String, value: Long) {
        val editor = mSharedPreferences!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return if (isKeyExists(key)) {
            mSharedPreferences!!.getLong(key, defaultValue)
        } else defaultValue
    }


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

    fun <T> saveObject(key: String, `object`: T) {
        val objectString = Gson().toJson(`object`)
        val editor = mSharedPreferences!!.edit()
        editor.putString(key, objectString)
        editor.apply()
    }

    fun <T> getObject(key: String, classType: Class<T>): T? {
        if (isKeyExists(key)) {
            val objectString = mSharedPreferences!!.getString(key, null)
            if (objectString != null) {
                return Gson().fromJson(objectString, classType)
            }
        }
        return null
    }


    fun <T> saveObjectsList(key: String, objectList: List<T>) {
        val objectString = Gson().toJson(objectList)
        val editor = mSharedPreferences!!.edit()
        editor.putString(key, objectString)
        editor.apply()
    }

    fun <T> getObjectsList(key: String, classType: Class<T>): List<T>? {
        if (isKeyExists(key)) {
            val objectString = mSharedPreferences!!.getString(key, null)
            if (objectString != null) {

                val t = Gson().fromJson<ArrayList<T>>(objectString, object : TypeToken<List<T>>() {

                }.type)

                val finalList = ArrayList<T>()

                for (i in t.indices) {
                    val s = t[i].toString()
                    finalList.add(Gson().fromJson(s, classType))
                }

                return finalList
            }
        }

        return null
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
