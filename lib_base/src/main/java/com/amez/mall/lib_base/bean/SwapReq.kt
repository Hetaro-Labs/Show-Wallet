package com.amez.mall.lib_base.bean

data class SwapReq(
    val userPublicKey:String,
    val quoteResponse:TokenPairUpdatedResp, //TODO Attempt to convert Bean to string, interface error missing XXX field.
    val computeUnitPriceMicroLamports:Int=100,
    val wrapAndUnwrapSol:Boolean=true,
    val useSharedAccounts:Boolean=true,
    val asLegacyTransaction:Boolean=false,
    val useTokenLedger:Boolean=false
)
