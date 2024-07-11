package com.showtime.wallet.net.bean

import kotlinx.serialization.Serializable

@Serializable
internal data class AppErrorResponse(
    val error: AppError,
    val id: Long,
    val jsonrpc: String,
)
