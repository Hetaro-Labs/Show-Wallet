package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.TransactionHistoryAdapter
import com.showtime.wallet.databinding.FragmentTransactionHistoryBinding
import com.showtime.wallet.vm.HistoryVModel

class TransactionHistoryFragment(val key:String?) : BaseProjFragment<FragmentTransactionHistoryBinding,HistoryVModel>(){

    private val testKey="EjAX2KePXZEZEaADMVc5UT2SQDvBYfoP1Jyx7frignFX"

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_transaction_history

    override fun FragmentTransactionHistoryBinding.initView(){

    }

    override fun initLiveDataObserve() {
        mViewModel.getTransactions.observeForever {
            //val adapter = TransactionHistoryAdapter(requireActivity(), it ,key?:"")
            val adapter = TransactionHistoryAdapter(requireActivity(), it ,testKey) //test key
            mBinding.rvTransactions.adapter = adapter
        }
    }

    override fun initRequestData() {
        //mViewModel.getTransactions(key?:"")
        mViewModel.getTransactions(testKey) //test key
    }
}