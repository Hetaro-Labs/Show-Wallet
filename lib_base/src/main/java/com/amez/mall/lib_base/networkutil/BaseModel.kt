package com.amez.mall.lib_base.networkutil

import android.text.TextUtils

/**
 * Describe:
 * Created by:DK
 * Created time:
 */
open class BaseModel<T> {
    var status: String? = null
    var code: Int? = null
    var msg: String? = null
    var data: T? = null
    fun isSuccess(): Boolean = TextUtils.equals("0", ""+code)
}