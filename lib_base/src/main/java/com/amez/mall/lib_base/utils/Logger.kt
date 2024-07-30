package com.amez.mall.lib_base.utils

import android.util.Log

object Logger {
    fun d(tag: String, msg: String){
        Log.e(tag, msg)
    }
}