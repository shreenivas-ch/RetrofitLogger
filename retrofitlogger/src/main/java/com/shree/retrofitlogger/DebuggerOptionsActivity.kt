package com.shree.retrofitlogger

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DebuggerOptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alertDialogSetDebug = AlertDialog.Builder(this).create()
        alertDialogSetDebug.setTitle(null)
        alertDialogSetDebug.setCancelable(false)
        alertDialogSetDebug.setMessage("Do you want to See APIs Debugger Logs")
        alertDialogSetDebug.setButton(AlertDialog.BUTTON_POSITIVE, "YES") { dialog, which ->
            dialog.dismiss()
            FastSave.getInstance().saveString("is_logs_active", "1")
            RetrofitLogger.getInstance().generateNotificationForLogs()
            finish()
        }
        alertDialogSetDebug.setButton(AlertDialog.BUTTON_NEGATIVE, "NO NEED") { dialog, which ->
            dialog.dismiss()
            FastSave.getInstance().saveString("is_logs_active", "0")
            finish()
        }

        alertDialogSetDebug.show()

    }
}
