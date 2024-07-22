package com.showtime.wallet.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.bean.SwapReq
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.net.ApiRequest

class SwapVModel :BaseViewModel() {

    private val TAG = SwapVModel::class.simpleName

    private val _getTokenPairUpdated = MutableLiveData<TokenPairUpdatedResp>()
    val getTokenPairUpdated: MutableLiveData<TokenPairUpdatedResp> = _getTokenPairUpdated

    fun getTokenPairUpdated(parameter1: String,parameter2: String,parameter3: Int){
        ApiRequest.getTokenPairUpdated(parameter1,parameter2,parameter3){
            _getTokenPairUpdated.postValue(it)
        }
    }

    fun doSwap(publicKey:String,quoteResponse:TokenPairUpdatedResp) {
        val req=SwapReq(publicKey,quoteResponse)
        ApiRequest.swap(req){
            Log.d(TAG,"swapTransaction==${it.swapTransaction}")
        }

        //TODO get data from SwapFragment
        // get serialized transactions for the swap
        /**const { swapTransaction } = await(
        await fetch ('https://quote-api.jup.ag/v6/swap', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json'
        },
        body: JSON.stringify({
        // quoteResponse from /quote api
        quoteResponse,
        // user public key to be used for the swap
        userPublicKey: wallet.publicKey.toString(),
        // auto wrap and unwrap SOL. default is true
        wrapAndUnwrapSol: true,
        })
        })
        ).json();

        // deserialize the transaction
        const swapTransactionBuf = Buffer . from (swapTransaction, 'base64');

        val sender = myAccount
        val connection = Connection(RpcUrl.MAINNNET)
        val blockhash = connection.getLatestBlockhash()
        val instruction =
        BaseInstruction(
        swapTransactionBuf, listOf(
        AccountMeta(myAccount.publicKey, writable = true, signer = true)
        ),
        SYSTEM_PROGRAM
        )
        val transaction =
        Transaction(blockhash, instruction, feePayer = sender.publicKey)
        transaction.sign(sender)

        try {
        val signature = connection.sendTransaction(transaction)
        //start TransactionStatusActivity and pass signature
        } catch (e: Exception) {
        }**/
    }

    enum class TokenTypeEnum(val value: String){
        TOKEN1("token1"),
        TOKEN2("token2")
    }
}