package com.showtime.wallet.net.bean

import kotlinx.serialization.Serializable

@Serializable
internal data class AppResponse<T>(
    val result: T,
    val id: Long,
    val jsonrpc: String,
)
