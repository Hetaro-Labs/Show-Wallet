package com.showtime.wallet.net.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Token(
    val mint: String,
    val tokenName: String,
    val symbol: String,
    val decimals: Int,
    val logo: String,
    var uiAmount: Double,
    val isNFT: Boolean,
    var tokenType:String,
    var tokenAccount:String
) : Parcelable