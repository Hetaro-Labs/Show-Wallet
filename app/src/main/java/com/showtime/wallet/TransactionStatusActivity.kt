package com.showtime.wallet

class TransactionStatusActivity {

    private fun checkTransactionStatus(signature: String) {
//        val response =
//            curl -X POST https://api.mainnet-beta.solana.com \
//        -H "Content-Type: application/json" \
//        -d '{
//        "jsonrpc": "2.0",
//        "id": 1,
//        "method": "getTransaction",
//        "params": [
//        signature,
//        {
//            "encoding": "json",
//            "commitment": "finalized"
//        }
//        ]
//    }'

//        if (response.result == null) {
//            //retry in 2s
//        }else{
//            if(response.result.meta.err == null){
//                //succeed
//                onTransactionSucceed()
//            }else{
//                //failed
//            }
//        }
    }

    private fun onTransactionSucceed(){
        //TODO update UI
        //TODO make WalletHome call getTokenAccountsByOwner to refresh balance
    }

}