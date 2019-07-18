package com.retrofitlogger.demo


import android.content.Context
import androidx.multidex.MultiDexApplication
import com.shree.retrofitlogger.RetrofitLogger

/**
 * Created by codename07 on 08/05/18.
 */


class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        RetrofitLogger.init(this, true)
    }

    companion object {

        private var context: Context? = null

        val instance: MainApplication
            get() = if (instance == null) MainApplication() else instance
    }

}
