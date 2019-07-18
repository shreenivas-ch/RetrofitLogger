package com.retrofitlogger.demo


/**
 * Created by shree on 31/05/18.
 */

interface OnImageListListener {
    fun onError(s: String)
    fun onSuccess(list: ArrayList<String>)
}
