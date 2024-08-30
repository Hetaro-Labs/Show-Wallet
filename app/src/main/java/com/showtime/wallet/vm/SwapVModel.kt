package com.showtime.wallet.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amez.mall.lib_base.bean.SwapReq
import com.amez.mall.lib_base.bean.SwapResp
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.net.ApiRequest
import com.showtime.wallet.net.QuickNodeUrl
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.sol4k.Connection
import org.sol4k.exception.RpcException
import org.sol4k.transaction.EncodedTransaction
import java.math.BigInteger
import java.util.Base64

class SwapVModel : BaseWalletVModel() {

    private val TAG = SwapVModel::class.simpleName

    private val _getTokenPairUpdated = MutableLiveData<TokenPairUpdatedResp>()
    val getTokenPairUpdated: MutableLiveData<TokenPairUpdatedResp> = _getTokenPairUpdated

    private val _getSwapTransaction = MutableLiveData<SwapResp>()
    val getSwapTransaction: MutableLiveData<SwapResp> = _getSwapTransaction

    fun getTokenPairUpdated(mint1: String, mint2: String, amount1: BigInteger) {
        ApiRequest.getTokenPairUpdated(mint1, mint2, amount1) {
            it?.let {
                _getTokenPairUpdated.postValue(it)
            }
        }
    }

    fun signTransactionAndSubmit(swapTransaction: String) {
        val transaction = EncodedTransaction.deserialize(swapTransaction)
        log("transaction: $transaction")
        transaction.sign(myAccount)

        val serialize = transaction.serialize()

        val encodedTransaction = Base64.getEncoder().encodeToString(serialize)
        log("encodedTransaction: $encodedTransaction")

        viewModelScope.launch {
            val connection = Connection(QuickNodeUrl.MAINNNET)

            try {
                val signature = connection.sendTransaction(transaction)
                _getTransactionHash.postValue(signature)
            } catch (e: RpcException) {
                log(e.rawResponse)

                val json = JSONObject(e.rawResponse)
                val logs = json.getJSONObject("error").getJSONObject("data").getJSONArray("logs")
                val sb = StringBuilder()

                for (i in 0 until logs.length()){
                    sb.append(logs.getString(i)).append("\n")
                }

                _getTransactionError.postValue(e.message + "\n" + sb.toString())
            }

        }
    }

    fun doSwap(quoteResponse: TokenPairUpdatedResp) {
        val req = SwapReq(myAccount.publicKey.toBase58(), quoteResponse)
        ApiRequest.swap(req) {
            Log.d(TAG, "swapTransaction==${it.swapTransaction}")
            _getSwapTransaction.postValue(it)
        }
    }

    enum class TokenTypeEnum(val value: String) {
        TOKEN1("token1"),
        TOKEN2("token2")
    }

}