package com.amez.mall.lib_base.utils

import android.util.Log

object Logger {
    fun d(tag: String, msg: String){
        if (msg.length > 4000) {
            Log.e(tag, msg.substring(0, 4000));
            d(tag, msg.substring(4000));
        } else{
            Log.e(tag, msg)
        }
    }
}