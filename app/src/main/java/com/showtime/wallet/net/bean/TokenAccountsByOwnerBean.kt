package com.showtime.wallet.net.bean


import kotlinx.serialization.Serializable

@Serializable
data class TokenAccountsByOwnerBean(
    val context:TokenAccountsByOwnerBeanContext,
    val value:List<TokenAccountsByOwnerBeanValue>
)
@Serializable
data class TokenAccountsByOwnerBeanContext(
    val apiVersion:String,
    val slot:String,
)
@Serializable
data class TokenAccountsByOwnerBeanValue(
    val account:TokenAccountsByOwnerBeanValueAcount
)
@Serializable
data class TokenAccountsByOwnerBeanValueAcount(
    val data:TokenAccountsByOwnerBeanValueAcountData
)
@Serializable
data class TokenAccountsByOwnerBeanValueAcountData(
    val parsed:TokenAccountsByOwnerBeanValueAcountDataParsed
)
@Serializable
data class TokenAccountsByOwnerBeanValueAcountDataParsed(
    val info:TokenAccountsByOwnerBeanValueAcountDataParsedInfo
)
@Serializable
data class TokenAccountsByOwnerBeanValueAcountDataParsedInfo(
    val isNative:Boolean,
    val mint:String,
    val owner:String,
    val state:String,
    val tokenAmount:TokenAccountsByOwnerBeanValueAcountDataParsedInfoTOkenAmount,
)
@Serializable
data class TokenAccountsByOwnerBeanValueAcountDataParsedInfoTOkenAmount(
    val amount:String,
    val decimals:Int,
    val uiAmount:Double,
    val uiAmountString:String,
)
