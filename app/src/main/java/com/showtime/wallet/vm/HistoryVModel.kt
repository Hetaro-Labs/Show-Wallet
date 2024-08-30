package com.showtime.wallet.vm

import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.bean.TransactionsData
import com.amez.mall.lib_base.bean.TransactionsResult
import com.amez.mall.lib_base.net.ApiRequest

class HistoryVModel : BaseWalletVModel() {

    private val _getTransactions = MutableLiveData<List<TransactionsResult>>()
    val getTransactions: MutableLiveData<List<TransactionsResult>> = _getTransactions

    fun getTransactions() {
        ApiRequest.getTransactions(key) {
            _getTransactions.postValue(it.results!!)
        }
    }

}