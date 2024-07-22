package com.amez.mall.lib_base.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenPairUpdatedResp(
    val inputMint:String?,
    val inAmount:String?,
    val outputMint:String?,
    val outAmount:String?,
    val otherAmountThreshold:String?,
    val swapMode:String?,
    val slippageBps:Int?,
    val platformFee:String?,
    val priceImpactPct:String?,
    val contextSlot:Int?,
    val timeTaken:Double?,
    val routePlan:List<TokenPairUpdateRoutePlan>?
): Parcelable {
    constructor() : this(null,null,null,null,null,null,null,
        null,null,null,null,null)
}

@Parcelize
data class TokenPairUpdateRoutePlan(
    val swapInfo:TokenPairUpdatedSwapInfo?,
    val percent:Int?
): Parcelable

@Parcelize
data class TokenPairUpdatedSwapInfo(
    val ammKey:String?,
    val label:String?,
    val inputMint:String?,
    val outputMint:String?,
    val inAmount:String?,
    val outAmount:String?,
    val feeAmount:String?,
    val feeMint:String?
): Parcelable
