package com.amez.mall.lib_base.bean


//data class TokenInfoResp(
//    val map: HashMap<String, TokenInfoResult>?
//)
data class TokenInfoResult(
    val mint:String,
    val decimals:Int,
    val tokenList:TokenInfoBeanData
)
data class TokenInfoBeanData(
    val name:String,
    val symbol:String,
    val image:String,
//    val mint:String,
//    val tokenName:String,
//    val symbol:String,
//    val decimals:Int,
//    val description:String,
//    val logo:String,
//    val tags:List<String>,
//    val verified:Boolean,
//    val network:List<String>,
//    val metadataToken:String
)