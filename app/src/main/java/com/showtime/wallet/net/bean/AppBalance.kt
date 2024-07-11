package com.showtime.wallet.net.bean

import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
internal data class AppBalance(
    @Serializable(with = AppBigIntegerSerializer::class)
    val value: BigInteger,
)