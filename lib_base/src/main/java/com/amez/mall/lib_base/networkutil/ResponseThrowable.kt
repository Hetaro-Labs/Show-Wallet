package com.amez.mall.lib_base.networkutil

/**
 * Describe:
 * Created by:DK
 * Created time:
 */
open class ResponseThrowable : Exception{
    var code: Int
    var errMsg: String

    constructor(error: ERROR, e: Throwable? = null) : super(e) {
        code = error.getKey()
        errMsg = error.getValue()
    }

    constructor(code: Int, msg: String, e: Throwable? = null) : super(e) {
        this.code = code
        this.errMsg = msg
    }

    constructor(base: BaseModel<*>, e: Throwable? = null) : super(e) {
        this.code = base.code!!
        this.errMsg = base.msg!!
    }
}