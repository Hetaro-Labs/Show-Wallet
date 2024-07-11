package com.amez.mall.lib_base.bean


data class TokenInfoResp(
    val status:String?,
    val message:String?,
    val result:List<TokenInfoResult>?,
)
data class TokenInfoResult(
    val tokenHash:String,
    val data:TokenInfoBeanData
)
data class TokenInfoBeanData(
    val mint:String,
    val tokenName:String,
    val symbol:String,
    val decimals:Double,
    val description:String,
    val logo:String,
    val tags:List<String>,
    val verified:Boolean,
    val network:List<String>,
    val metadataToken:String
)