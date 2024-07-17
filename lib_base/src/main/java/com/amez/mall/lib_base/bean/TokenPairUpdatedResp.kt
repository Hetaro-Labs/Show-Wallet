package com.amez.mall.lib_base.bean

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
    val contextSlot:String?,
    val timeTaken:String?,
    val routePlan:List<TokenPairUpdateRoutePlan>?
) {
    constructor() : this(null,null,null,null,null,null,null,
        null,null,null,null,null)
}

data class TokenPairUpdateRoutePlan(
    val swapInfo:TokenPairUpdatedSwapInfo?,
    val percent:Int?
)
data class TokenPairUpdatedSwapInfo(
    val ammKey:String?,
    val label:String?,
    val inputMint:String?,
    val outputMint:String?,
    val inAmount:String?,
    val outAmount:String?,
    val feeAmount:String?,
    val feeMint:String?
)
