package com.amez.mall.lib_base.bean

data class SwapResp(
    val swapTransaction:String,
    val lastValidBlockHeight:Long,
    val prioritizationFeeLamports:Int
)
