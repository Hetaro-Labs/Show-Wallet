package com.showtime.wallet.net.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigInteger
import kotlin.math.pow

@Parcelize
data class Token(
    val mint: String,
    val tokenName: String,
    val symbol: String,
    val decimals: Int,
    val logo: String,
    var uiAmount: Double,
    val isNFT: Boolean,
    var tokenType: String,
    var tokenAccount: String,
    var amountInUsd: Double = 0.0
) : Parcelable {

    fun getAmount(): BigInteger = BigInteger.valueOf((uiAmount * 10.0.pow(decimals)).toLong())

    fun copy(): Token {
        return Token(
            mint, tokenName, symbol, decimals, logo, uiAmount, isNFT, tokenType, tokenAccount, amountInUsd
        )
    }

    override fun hashCode(): Int {
        return mint.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Token && other.mint == mint
    }

    override fun toString(): String {
        return "$tokenName, $mint, $uiAmount, $amountInUsd"
    }

}