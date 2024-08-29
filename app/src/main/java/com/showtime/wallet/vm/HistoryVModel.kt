package com.showtime.wallet.vm

import androidx.lifecycle.MutableLiveData
import com.amez.mall.lib_base.base.mvvm.vm.BaseViewModel
import com.amez.mall.lib_base.bean.TransactionsData
import com.amez.mall.lib_base.net.ApiRequest

class HistoryVModel :BaseWalletVModel() {

    private val _getTransactions = MutableLiveData<List<TransactionsData>>()
    val getTransactions: MutableLiveData<List<TransactionsData>> = _getTransactions

    fun getTransactions(){
        ApiRequest.getTransactions(myAccount.publicKey.toBase58()){
            val transList= mutableListOf<TransactionsData>()
            it.results?.forEach { transList.addAll(it.data!!) }
            val finalList=transList.filter { it.action == "transfer" } //TODO Filter here as needed first
            _getTransactions.postValue(finalList)
        }
    }

}