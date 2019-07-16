package com.shree.retrofitlogger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class RetrofitLogger {

    private var context: Context? = null

    companion object {

        private var instance: RetrofitLogger? = null

        fun init(ctx: Context) {
            FastSave.init(ctx)
            getInstance().setupLifecycleListener()
            getInstance().context = ctx.applicationContext
        }

        fun getInstance(): RetrofitLogger {
            if (instance == null) {
                synchronized(RetrofitLogger::class.java) {
                    if (instance == null) {
                        instance = RetrofitLogger()
                    }
                }
            }
            return instance!!
        }
    }

    private val lifecycleListener = MyLifecycleListener(object : AppResumePauseListner {
            override fun onResume() {
                if (BuildConfig.DEBUG) {
                    var is_logs_active = FastSave.getInstance().getString("is_logs_active", null)
                    if (is_logs_active != null && is_logs_active == "1") {
                        generateNotificationForLogs()
                    }
                }
            }

            override fun onPause() {
                if (BuildConfig.DEBUG) {
                    val nMgr = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nMgr.cancel(-1)
                }
            }

        })

    private fun setupLifecycleListener() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleListener)
    }

    fun showDebugOptions() {
        var is_logs_active = FastSave.getInstance().getString("is_logs_active", null)
        if (is_logs_active == null) {
            apiDebuggingOptions()
        }
    }

    private fun apiDebuggingOptions() {

        var intent = Intent(context, DebuggerOptionsActivity::class.java)
        context?.startActivity(intent)
    }

    fun generateNotificationForLogs() {

        val notifyIntent = Intent(context, LogsActivity::class.java)

        var notifyPendingIntent = PendingIntent.getActivity(
            context, 0,
            notifyIntent, PendingIntent.FLAG_ONE_SHOT
        )

        val nm = context?.getSystemService(
            Context
                .NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "id", "name",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setSound(null, null)
            channel.setShowBadge(false)
            nm.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context!!, "id")
        builder.setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.bug)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context?.resources,
                    R.drawable.bug
                )
            )
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Click Here for " + getApplicationName() + " Logs ")
            .setColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
            .setContentIntent(notifyPendingIntent)
            .setOngoing(true)
            .setWhen(0)

        var notification = builder.build()
        notification.flags = NotificationCompat.FLAG_NO_CLEAR
        nm.notify(-1, notification)

    }

    fun getApplicationName(): String {
        var ai: ApplicationInfo?
        try {
            ai = context?.packageManager?.getApplicationInfo(context!!.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            ai = null
        }

        val applicationName = if (ai != null) context?.packageManager?.getApplicationLabel(ai) else "(unknown)"

        return applicationName.toString()
    }

    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message ->

            if (BuildConfig.DEBUG) {
                if (isJSONValid(message)) {
                    Log.i("OkHttp", formatString(message))
                } else {
                    Log.i("OkHttp", message)
                }

                val log = FastSave.getInstance().getString(Constants.key_spm_logs, null)
                if (log == "" || log == null) {
                    FastSave.getInstance().saveString(Constants.key_spm_logs, message + "\n")
                } else {
                    FastSave.getInstance().saveString(Constants.key_spm_logs, message + "\n" + log)
                }
            }
        }

        logging.level = HttpLoggingInterceptor.Level.HEADERS
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    fun isJSONValid(test: String): Boolean {
        try {
            JSONObject(test)
        } catch (ex: JSONException) {
            try {
                JSONArray(test)
            } catch (ex1: JSONException) {
                return false
            }

        }
        return true
    }

    fun formatString(text: String): String {

        val json = StringBuilder()
        var indentString = ""

        for (i in 0 until text.length) {
            val letter = text[i]
            when (letter) {
                '{', '[' -> {
                    json.append("\n" + indentString + letter + "\n")
                    indentString += "\t"
                    json.append(indentString)
                }
                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    json.append("\n" + indentString + letter)
                }
                ',' -> json.append(letter + "\n" + indentString)

                else -> json.append(letter)
            }
        }

        return json.toString()
    }
}