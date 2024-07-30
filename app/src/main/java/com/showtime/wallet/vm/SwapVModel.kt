package com.showtime.wallet.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.bean.SwapReq
import com.amez.mall.lib_base.bean.SwapResp
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.net.ApiRequest
import com.showtime.wallet.net.bean.Token
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.sol4k.Connection
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.exception.RpcException
import java.lang.RuntimeException
import java.math.BigInteger

class SwapVModel : BaseViewModel() {

    private val TAG = SwapVModel::class.simpleName

    private val _getTokenPairUpdated = MutableLiveData<TokenPairUpdatedResp>()
    val getTokenPairUpdated: MutableLiveData<TokenPairUpdatedResp> = _getTokenPairUpdated

    private val _getSwapTransaction = MutableLiveData<SwapResp>()
    val getSwapTransaction: MutableLiveData<SwapResp> = _getSwapTransaction

    private val _getTokenAccountBalance = MutableLiveData<UpdateTokenAccountBalance>()
    val getTokenAccountBalance: MutableLiveData<UpdateTokenAccountBalance> = _getTokenAccountBalance

    private val _getTokenAccountBalanceErr = MutableLiveData<String>()
    val getTokenAccountBalanceErr: MutableLiveData<String> = _getTokenAccountBalanceErr

    fun getTokenPairUpdated(parameter1: String, parameter2: String, parameter3: BigInteger) {
        ApiRequest.getTokenPairUpdated(parameter1, parameter2, parameter3) {
            _getTokenPairUpdated.postValue(it)
        }
    }

    fun doSwap(publicKey: String, quoteResponse: TokenPairUpdatedResp) {
        val req = SwapReq(publicKey, quoteResponse)
        ApiRequest.swap(req) {
            Log.d(TAG, "swapTransaction==${it.swapTransaction}")
            _getSwapTransaction.postValue(it)
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
        }

         **/
    }

    fun getTokenAccountBalance(token:Token,type:String=""){
        val coroutineExceptionHandler = CoroutineExceptionHandler {coroutineContext, throwable ->

        }
        GlobalScope.launch(coroutineExceptionHandler) {
            try {
                val response = async {
                    val connection = Connection(RpcUrl.DEVNET)
                    //connection.getTokenAccountBalance(PublicKey(token.tokenAccount))
                    connection.getTokenAccountBalance(PublicKey("73d3sqQPLsiwKvdJt2XnnLEzNiEjfn2nreqLujM7zXiT")) //Test Key
                }
                val bean=response.await()
                Log.d(TAG, "getTokenAccountBalance==${bean}")
                _getTokenAccountBalance.postValue(UpdateTokenAccountBalance(bean.uiAmount,type))
            }catch (e: RuntimeException){
                _getTokenAccountBalanceErr.postValue(e.message)
            }
            catch (e: RpcException){
                _getTokenAccountBalanceErr.postValue(e.message)
            }
        }
    }

    enum class TokenTypeEnum(val value: String) {
        TOKEN1("token1"),
        TOKEN2("token2")
    }

    data class UpdateTokenAccountBalance(
        val amount:String,
        val type:String
    )
}