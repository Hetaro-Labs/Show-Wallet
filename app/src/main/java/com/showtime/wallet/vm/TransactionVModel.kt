package com.showtime.wallet.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.showtime.wallet.net.AppConnection
import com.showtime.wallet.net.QuickNodeUrl
import com.showtime.wallet.net.bean.TransactionStatusResp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sol4k.exception.RpcException

class TransactionVModel : BaseViewModel() {

    private val _getTransaction = MutableLiveData<TransactionStatusResp?>()
    val getTransaction: MutableLiveData<TransactionStatusResp?> = _getTransaction

    fun getTransaction(signature: String) {
        viewModelScope.launch {
            try {
                val connection = AppConnection(QuickNodeUrl.MAINNNET)
                val bean = withContext(Dispatchers.IO) {
                    connection.getTransaction(signature)
                }

                _getTransaction.postValue(bean)
            } catch (e: RuntimeException) {
                defUI.toastEvent.postValue(e.message)
            } catch (e: RpcException) {
                defUI.toastEvent.postValue(e.message)
            }
        }
    }

}