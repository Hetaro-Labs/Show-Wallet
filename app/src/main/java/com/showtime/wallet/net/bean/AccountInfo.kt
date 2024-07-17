package com.showtime.wallet.net.bean

import kotlinx.serialization.Serializable

@Serializable
data class AccountInfo(
    val value:AccountInfoValue
)
@Serializable
data class AccountInfoValue(
    val data:AccountInfoData
)
@Serializable
data class AccountInfoData(
    val parsed:AccountInfoParsed
)
@Serializable
data class AccountInfoParsed(
    val info:AccountInfoInfo,
    val type:String
)
@Serializable
data class AccountInfoInfo(
    val programData:String
)
