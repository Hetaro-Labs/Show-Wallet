package com.amez.mall.lib_base.bean

data class GetAssetsByOwnerReq(
    val params:GetAssetsByOwnerReqParams,
    val jsonrpc:String="2.0",
    val id:String="my-id",
    val method:String="getAssetsByOwner"
)
data class GetAssetsByOwnerReqParams(
    val ownerAddress:String,
    val page:Int=1,
    val limit:Int=1000
)

