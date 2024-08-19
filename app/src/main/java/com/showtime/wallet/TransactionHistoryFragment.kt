package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.TransactionHistoryAdapter
import com.showtime.wallet.databinding.FragmentTransactionHistoryBinding
import com.showtime.wallet.vm.HistoryVModel

class TransactionHistoryFragment() : BaseSecondaryFragment<FragmentTransactionHistoryBinding,HistoryVModel>(){

    override fun getContentViewLayoutID() = R.layout.fragment_transaction_history

    override fun FragmentTransactionHistoryBinding.initView(){
        mBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.getTransactions(key) //test key
        }
    }

    override fun initLiveDataObserve() {
        mViewModel.getTransactions.observeForever {
            mBinding.swipeRefresh.isRefreshing = false
            val adapter = TransactionHistoryAdapter(requireActivity(), it ,key) //test key
            mBinding.rvTransactions.adapter = adapter
        }
    }

    override fun initRequestData() {
        mBinding.swipeRefresh.isRefreshing = true
        mViewModel.getTransactions(key)
    }
}