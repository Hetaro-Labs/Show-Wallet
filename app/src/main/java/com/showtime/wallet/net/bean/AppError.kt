package com.showtime.wallet.net.bean

import kotlinx.serialization.Serializable

@Serializable
internal data class AppError(
    val code: Int,
    val message: String,
)