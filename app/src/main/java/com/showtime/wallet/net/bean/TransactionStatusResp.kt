package com.showtime.wallet.net.bean

import kotlinx.serialization.Serializable

/**
 * {"jsonrpc":"2.0","error":{"code":-32015,"message":"Transaction version (0) is not supported by the requesting client. Please try the request again with the following configuration parameter: \"maxSupportedTransactionVersion\": 0"},"id":1724830817511}
 *
 */
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
