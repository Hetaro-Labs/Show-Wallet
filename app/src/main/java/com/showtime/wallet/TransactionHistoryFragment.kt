package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.TransactionHistoryAdapter
import com.showtime.wallet.databinding.FragmentTransactionHistoryBinding
import com.showtime.wallet.vm.HistoryVModel

//TODO use arguments to pass key, like @WalletHomeFragment
class TransactionHistoryFragment(val key:String) : BaseProjFragment<FragmentTransactionHistoryBinding,HistoryVModel>(){

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_transaction_history

    override fun FragmentTransactionHistoryBinding.initView(){

    }

    override fun initLiveDataObserve() {
        mViewModel.getTransactions.observeForever {
            //val adapter = TransactionHistoryAdapter(requireActivity(), it ,key?:"")
            val adapter = TransactionHistoryAdapter(requireActivity(), it ,key) //test key
            mBinding.rvTransactions.adapter = adapter
        }
    }

    override fun initRequestData() {
        //mViewModel.getTransactions(key?:"")
        mViewModel.getTransactions(key) //test key
    }
}