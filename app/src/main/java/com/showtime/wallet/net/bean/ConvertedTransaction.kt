package com.showtime.wallet.net.bean

import com.amez.mall.lib_base.bean.TransactionsResult
import com.showtime.wallet.DefaultTokenListData
import com.showtime.wallet.utils.TokenListCache
import kotlin.math.pow

class ConvertedTransaction(
    val type: Int, val handler: String,
    val inAmount: String?, val outAmount: String?, val timestamp: String,
    val icon1: String? = null, val icon2: String? = null
) {

    companion object {

        val TYPE_DK = 0
        val TYPE_SEND = 1
        val TYPE_RECEIVE = 2
        val TYPE_SWAP = 3

        fun from(myPublicKey: String, result: TransactionsResult): ConvertedTransaction {
            var outAmount: String? = null
            var inAmount: String? = null

            var icon1: String? = null
            var icon2: String? = null

            var handler = ""
            var timestamp = ""

            for (item in result.data!!) {
                item.timestamp?.let {
                    timestamp = it
                }

                if (item.action == "transfer") {
                    if (item.source != item.destination) {
                        val token = if (item.token?.isNotEmpty() == true) TokenListCache.getList().findLast { it.mint == item.token }
                        else DefaultTokenListData.SOL

                        val amount = if (token != null) {
                            (item.amount / 10.0.pow(token.decimals)).toString() + " " + token.symbol
                        } else {
                            item.amount.toString()
                        }

                        if (item.source == myPublicKey) {
                            outAmount = amount
                            handler = item.destination!!
                            icon1 = token?.logo
                        } else if (item.destination == myPublicKey) {
                            inAmount = amount
                            handler = item.source!!
                            icon2 = token?.logo
                        }
                    }
                }
            }

            return if (inAmount == null && outAmount == null) {
                ConvertedTransaction(TYPE_DK, handler, null, null, timestamp)
            } else if (inAmount == null) {
                ConvertedTransaction(TYPE_SEND, handler, null, outAmount, timestamp, icon1, icon2)
            } else if (outAmount == null) {
                ConvertedTransaction(TYPE_RECEIVE, handler, inAmount, null, timestamp, icon1, icon2)
            } else {
                ConvertedTransaction(TYPE_SWAP, "Jupiter", inAmount, outAmount, timestamp, icon1, icon2)
            }
        }
    }
}