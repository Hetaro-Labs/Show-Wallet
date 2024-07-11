package com.amez.mall.lib_base.bean


data class TokenInfoReq(
    val hydration:Hydration,
    val tokenHashes:List<String>
)

data class Hydration(
    val accountHash:Boolean=true
)