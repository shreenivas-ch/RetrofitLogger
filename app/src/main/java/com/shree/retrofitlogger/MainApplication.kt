package com.shree.retrofitlogger


import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication

/**
 * Created by codename07 on 08/05/18.
 */


class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {

        private var context: Context? = null

        val instance: MainApplication
            get() = if (instance == null) MainApplication() else instance
    }

}
