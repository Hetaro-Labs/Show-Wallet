package com.showtime.wallet.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.sol4k.Connection
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.exception.RpcException
import java.lang.RuntimeException

class SendTokenVModel : BaseViewModel() {

    private val TAG = SendTokenVModel::class.simpleName

    private val _getTokenAccountBalance = MutableLiveData<UpdateTokenAccountBalance>()
    val getTokenAccountBalance: MutableLiveData<UpdateTokenAccountBalance> = _getTokenAccountBalance

    private val _getEnterAmountErr = MutableLiveData<Boolean>()
    val getEnterAmountErr: MutableLiveData<Boolean> = _getEnterAmountErr

    private val _getAddressErr = MutableLiveData<Boolean>()
    val getAddressErr: MutableLiveData<Boolean> = _getAddressErr

    fun onAddressChange(it: String){
        if (CryptoUtils.isValidSolanaAddress(it)) {
            _getAddressErr.postValue(false)
        } else {
            _getAddressErr.postValue(true)
        }
    }

    fun onAmountChange(token: Token, it: String){
        val amount = if(it.isEmpty()) 0.0 else it.toDouble()

        if (amount > token.uiAmount || amount <= 0.0) {
            _getEnterAmountErr.postValue(true)
        } else {
            _getEnterAmountErr.postValue(false)
        }
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
                _getTokenAccountBalance.postValue(
                    UpdateTokenAccountBalance(
                        bean.uiAmount,
                        type
                    )
                )
            }catch (e: RuntimeException){
            } catch (e: RpcException){
            }
        }
    }

    data class UpdateTokenAccountBalance(
        val amount:String,
        val type:String
    )

}