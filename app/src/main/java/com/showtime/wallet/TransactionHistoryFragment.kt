package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.TransactionHistoryAdapter
import com.showtime.wallet.databinding.FragmentTransactionHistoryBinding
import com.showtime.wallet.net.bean.ConvertedTransaction
import com.showtime.wallet.vm.HistoryVModel
import okhttp3.internal.format

class TransactionHistoryFragment() :
    BaseSecondaryFragment<FragmentTransactionHistoryBinding, HistoryVModel>() {

    override fun getContentViewLayoutID() = R.layout.fragment_transaction_history

    override fun FragmentTransactionHistoryBinding.initView() {
        mBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.getTransactions() //test key
        }
    }

    override fun initLiveDataObserve() {
        mViewModel.getTransactions.observeForever {
            for(item in it){
                log("===========transaction===========")
                for (d in item.data!!){
                    log("[${d.action}] ${d.token} from=${d.source}, to=${d.destination}, amt=${d.amount}")
                }
            }
            val data = it.map { result -> ConvertedTransaction.from(key, result) }

            mBinding.swipeRefresh.isRefreshing = false
            val adapter = TransactionHistoryAdapter(requireActivity(), data, key) //test key
            mBinding.rvTransactions.adapter = adapter
        }
    }

    override fun initRequestData() {
        mBinding.swipeRefresh.isRefreshing = true
        mViewModel.getTransactions()
    }

}