package com.shree.retrofitlogger

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_logs.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LogsActivity : AppCompatActivity() {

    lateinit var adapter: LogsAdapter
    var arrLogs = ArrayList<ModelLog>()

    var foundAtArray = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        var highlighter = TextHighlighter()
            .setBackgroundColor(Color.parseColor("#FFFF00"))
            .setForegroundColor(Color.BLACK)
            .setBold(true)
            .setItalic(true)

        rcLogs.layoutManager = LinearLayoutManager(this)
        adapter = LogsAdapter(arrLogs, highlighter)
        rcLogs.adapter = adapter

        loadLogs()

        imgClear.setOnClickListener {
            et_search.setText("")
        }

        imgMenu.setOnClickListener {
            showMenu()
        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(str: CharSequence, p1: Int, p2: Int, p3: Int) {

                foundAtArray.clear()

                if (str.isNotEmpty()) {
                    adapter.textToHightlight = str.toString()
                    for (i in arrLogs.indices) {
                        if (arrLogs[i].logstring.toLowerCase().contains(str.toString().toLowerCase())) {
                            foundAtArray.add(i)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    if (foundAtArray.size > 0) {
                        rcLogs.scrollToPosition(foundAtArray[0])
                    }
                } else {
                    adapter.textToHightlight = ""
                    adapter.notifyDataSetChanged()
                }
            }

        })


    }

    fun showMenu() {

        val builderSingle = AlertDialog.Builder(this)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        arrayAdapter.add("Clear Logs")
        arrayAdapter.add("De-Activate")

        builderSingle.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }

        builderSingle.setAdapter(arrayAdapter) { dialog, which ->

            if (which == 0) {
                FastSave.getInstance().saveString(Constants.key_spm_logs, "")
                loadLogs()
            } else if (which == 1) {
                val alertDialogSetDebug = AlertDialog.Builder(this@LogsActivity).create()
                alertDialogSetDebug.setTitle(null)
                alertDialogSetDebug.setMessage("Are you sure, you don't want to see Debugger logs?")
                alertDialogSetDebug.setButton(AlertDialog.BUTTON_POSITIVE, "YES, DE-ACTIVATE") { dialog, which ->
                    dialog.dismiss()
                    FastSave.getInstance().saveString(Constants.key_spm_logs, "")
                    FastSave.getInstance().saveString("is_logs_active", "0")
                    finish()
                }
                alertDialogSetDebug.setButton(AlertDialog.BUTTON_NEGATIVE, "DISMISS") { dialog, which ->
                    dialog.dismiss()
                }

                alertDialogSetDebug.show()
            }
        }
        builderSingle.show()
    }

    fun loadLogs() {

        arrLogs.clear()
        val lines: List<String>? =
            FastSave.getInstance().getString(Constants.key_spm_logs, null)?.split(System.getProperty("line.separator"))
        if (lines != null) {
            for (i in lines!!.indices) {
                when {
                    isJSONValid(lines[i]) -> {
                        var modellog = ModelLog(formatString(lines[i]), "json")
                        arrLogs.add(modellog)
                    }

                    lines[i].contains("http") -> {
                        var modellog = ModelLog(formatString(lines[i]), "http")
                        arrLogs.add(modellog)
                    }
                    else -> {
                        var modellog = ModelLog(formatString(lines[i]))
                        arrLogs.add(modellog)
                    }
                }
            }
        }
        adapter.notifyDataSetChanged()

        if (FastSave.getInstance().getString(Constants.key_spm_logs, null) == "") {
            txtNoLogs.visibility = View.VISIBLE
        } else {
            txtNoLogs.visibility = View.GONE
        }

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

    class ModelLog(
        var logstring: String = "",
        var logtype: String = ""
    )

    class LogsAdapter(private var listLogs: ArrayList<ModelLog>, var highlighter: TextHighlighter) :
        RecyclerView.Adapter<LogsAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            bindViews(holder, position)
        }

        private fun bindViews(holder: MyViewHolder, position: Int) {

            if (textToHightlight == "") {
                holder.txt_log.text = listLogs[position].logstring
            } else {
                holder.txt_log.text = highlighter.highlightString(
                    listLogs[position].logstring,
                    textToHightlight,
                    TextHighlighter.BASE_MATCHER
                )
            }

            if (listLogs[position].logtype == "http") {
                holder.txt_log.setTextColor(Color.parseColor("#3B14BE"))
            } else if (listLogs[position].logtype == "json") {
                holder.txt_log.setTextColor(Color.parseColor("#8C0032"))
            } else {
                holder.txt_log.setTextColor(Color.parseColor("#000000"))
            }
        }

        override fun getItemCount(): Int {
            return listLogs.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val txt_log: TextView = itemView.findViewById(R.id.txt_log) as TextView
        }

        var textToHightlight = ""

        fun setTextToHighLight(textToHightlight: String) {
            this.textToHightlight = textToHightlight
        }
    }
}
