package com.amez.mall.lib_base.utils

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


object DateUtils {
    fun timestampToAmericanDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
        val date = Date(timestamp)
        return sdf.format(date)
    }
}