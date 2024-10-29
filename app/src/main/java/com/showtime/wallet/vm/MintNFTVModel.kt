package com.showtime.wallet.vm

import androidx.lifecycle.viewModelScope
import com.amez.mall.lib_base.bean.MintNFTReq
import com.amez.mall.lib_base.net.ApiRequest
import com.showtime.wallet.net.QuickNodeUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sol4k.Connection
import org.sol4k.exception.RpcException
import org.sol4k.transaction.EncodedTransaction
import java.util.Base64

class MintNFTVModel: BaseWalletVModel() {

    fun mint(receiver: String, tier: Int){
        ApiRequest.mintNFT(MintNFTReq(key, receiver, tier.toString())){
            val tx = it.tx
            log("unsign transaction: $tx")

            val transaction = EncodedTransaction.deserialize(tx) //Transaction.from(tx)
            transaction.sign(myAccount)

            val serialize = transaction.serialize()

            val encodedTransaction = Base64.getEncoder().encodeToString(serialize)
            log("signed transaction: $encodedTransaction")

            viewModelScope.launch {
                val connection = Connection(QuickNodeUrl.MAINNNET)
//                val connection = Connection(RpcUrl.DEVNET)

                try {
                    val signature = withContext(Dispatchers.IO){
                        connection.sendTransaction(transaction)
                    }

                    log("signature: $signature")
                    _getTransactionHash.postValue(signature)
                } catch (e: RpcException) {
                    log(e.rawResponse)

//                    val json = JSONObject(e.rawResponse)
//                    val logs = json.getJSONObject("error").getJSONObject("data").getJSONArray("logs")
//                    val sb = StringBuilder()
//
//                    for (i in 0 until logs.length()){
//                        sb.append(logs.getString(i)).append("\n")
//                    }

                    _getTransactionError.postValue(e.message)
                }

            }
        }
    }
}