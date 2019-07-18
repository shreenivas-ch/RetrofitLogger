package com.shree.retrofitlogger

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FloatingService : Service() {
    lateinit var floatingImage: FloatingImage

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        floatingImage = FloatingImage(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingImage.destroy()
    }
}