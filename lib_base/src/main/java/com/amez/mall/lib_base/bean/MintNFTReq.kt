package com.amez.mall.lib_base.bean

data class MintNFTReq(
    val payer: String,
    val receiver: String,
    val mint_type: String
)