package com.showtime.wallet.vm

import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.showtime.wallet.net.AppConnection
import com.showtime.wallet.net.QuickNodeUrl
import com.showtime.wallet.net.bean.TransactionStatusResp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.sol4k.RpcUrl
import org.sol4k.exception.RpcException
import java.lang.RuntimeException

class TransactionVModel : BaseViewModel() {

    private val _getTransaction = MutableLiveData<TransactionStatusResp?>()
    val getTransaction: MutableLiveData<TransactionStatusResp?> = _getTransaction

    fun getTransaction(signature: String){
        val coroutineExceptionHandler = CoroutineExceptionHandler {coroutineContext, throwable ->

        }
        GlobalScope.launch(coroutineExceptionHandler) {
            try {
                val response = async {
                    val connection = AppConnection(QuickNodeUrl.MAINNNET)
                    connection.getTransaction(signature)
                }
                val bean=response.await()
                _getTransaction.postValue(bean)
            }catch (e: RuntimeException){
                defUI.toastEvent.postValue(e.message)
            }
            catch (e: RpcException){
                defUI.toastEvent.postValue(e.message)
            }
        }
    }

}