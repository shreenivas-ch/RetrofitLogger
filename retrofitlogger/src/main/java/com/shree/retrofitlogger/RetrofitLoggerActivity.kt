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
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

abstract class RetrofitLoggerActivity : AppCompatActivity() {
    private fun apiDebuggingOptions() {

        val alertDialogSetDebug = AlertDialog.Builder(this@RetrofitLoggerActivity).create()
        alertDialogSetDebug.setTitle(null)
        alertDialogSetDebug.setMessage("Do you want to See APIs Debugger Logs")
        alertDialogSetDebug.setButton(AlertDialog.BUTTON_POSITIVE, "YES") { dialog, which ->
            dialog.dismiss()

            android.app.AlertDialog.Builder(this@RetrofitLoggerActivity)
                .setTitle("Select Type")
                .setMessage("Select 'NOTIFICATION' if overlay not working in your phone.")
                .setPositiveButton("OVERLAY BUG") { dialog, which ->
                    dialog.dismiss()
                    FastSave.getInstance().saveString("is_logs_active", "1")
                    FastSave.getInstance().saveString("is_logs_active_type", "overlay")
                    showOverLayPermission()

                }.setNegativeButton("NOTIFICATION") { dialog, which ->
                    dialog.dismiss()
                    FastSave.getInstance().saveString("is_logs_active", "1")
                    FastSave.getInstance().saveString("is_logs_active_type", "notification")
                    generateNotificationForLogs(this@RetrofitLoggerActivity)
                }
                .show()

        }
        alertDialogSetDebug.setButton(AlertDialog.BUTTON_NEGATIVE, "NO NEED") { dialog, which ->
            dialog.dismiss()
            FastSave.getInstance().saveString("is_logs_active", "0")
        }

        alertDialogSetDebug.show()
    }

    fun generateNotificationForLogs(ctx: Context) {

        val notifyIntent = Intent(ctx, LogsActivity::class.java)

        var notifyPendingIntent = PendingIntent.getActivity(
            ctx, 0,
            notifyIntent, PendingIntent.FLAG_ONE_SHOT
        )

        val nm = ctx.getSystemService(
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

        val builder = NotificationCompat.Builder(this, "id")
        builder.setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.bug)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.bug
                )
            )
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Click Here for " + getApplicationName() + " Logs ")
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
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
            ai = packageManager?.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            ai = null
        }

        val applicationName = if (ai != null) packageManager?.getApplicationLabel(ai) else "(unknown)"

        return applicationName.toString()
    }

    private fun showOverLayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                val alertDialog = AlertDialog.Builder(this@RetrofitLoggerActivity).create()
                alertDialog.setTitle(null)
                alertDialog.setMessage("To See API responses activate 'Allow display over other Apps' permission")
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, which ->

                    dialog.dismiss()
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)

                }
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL") { dialog, which ->

                    dialog.dismiss()
                }

                alertDialog.show()
            } else {
                startService(Intent(application, FloatingService::class.java))
            }
        } else {
            startService(Intent(application, FloatingService::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        if (RetrofitLogger.getInstance().getIsActive()) {
            FastSave.init(this)
            var is_logs_active = FastSave.getInstance().getString("is_logs_active", null)
            if (is_logs_active == null) {
                apiDebuggingOptions()
            } else if (is_logs_active == "1") {
                var is_logs_active_type = FastSave.getInstance().getString("is_logs_active_type", "notification")
                if (is_logs_active_type == "overlay") {
                    showOverLayPermission()
                } else {
                    generateNotificationForLogs(this@RetrofitLoggerActivity)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (RetrofitLogger.getInstance().getIsActive()) {
            stopService(Intent(application, FloatingService::class.java))
            val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nMgr.cancel(-1)
        }
    }
}