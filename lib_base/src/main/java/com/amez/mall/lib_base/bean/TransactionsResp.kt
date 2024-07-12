package com.amez.mall.lib_base.bean

data class TransactionsResp(
    val status:String?,
    val message:String?,
    val results:List<TransactionsResult>?,
    val pagination:PaginationBase?
)
data class TransactionsResult(
    val transactionHash:String?,
    val data:List<TransactionsData>?
)
data class TransactionsData(
    val instructionIndex:Int?,
    val innerInstructionIndex:Int?,
    val action:String?,
    val status:String?,
    val source:String?,
    val sourceAssociation:String?,
    val destination:String?,
    val destinationAssociation:String?,
    val token:String?,
    val amount:Double,
    val timestamp:String?
)