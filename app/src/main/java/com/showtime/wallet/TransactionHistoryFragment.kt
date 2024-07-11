package com.showtime.wallet

class TransactionHistoryFragment {

    //TODO layout_transaction_history
    // rv_transactions.adapter = item layout: layout_transaction
    // for each item click: go TransactionDetailActivity
    // data from getTransactions.curl
    // if action == "transfer" ->
    //   if source == myAccount -> tv_transaction_type.text = "sent"
    //   if destination == myAccount -> tv_transaction_type.text = "receive"
    //   if token == "" ->
    //      icon_token.setImageResource(R.drawable.ic_solana)
    //      tv_amount.text = amount / 10^8 + "SOL"
    //   else call getTokenInfo
    //      icon_token.setImageResource(info.logo)
    //      tv_amount.text = amount / 10^info.decimals + info.symbol

}