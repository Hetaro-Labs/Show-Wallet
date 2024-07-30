package com.showtime.wallet.net.bean

import kotlinx.serialization.Serializable

@Serializable
data class TransactionStatusResp(
    val meta:TransactionStatusMeta
)
@Serializable
data class TransactionStatusMeta(
    val computeUnitsConsumed:Int,
    val fee:Int,
    val err:TransactionErr?
)
@Serializable
data class TransactionErr(
    val code: Int,
    val message: String
)
