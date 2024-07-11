package com.showtime.wallet.net.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Token(
    val mint: String,
    val tokenName: String,
    val symbol: String,
    val decimals: Double,
    val logo: String,
    val uiAmount: Double,
) : Parcelable