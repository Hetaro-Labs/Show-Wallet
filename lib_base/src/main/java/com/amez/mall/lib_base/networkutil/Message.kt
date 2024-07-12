package com.amez.mall.lib_base.networkutil

/**
 * Describe:
 */
class Message @JvmOverloads constructor(
    var code: Int = 0,
    var msg: String = "",
    var arg1: Int = 0,
    var arg2: Int = 0,
    var obj: Any? = null
)