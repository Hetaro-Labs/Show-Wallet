package com.showtime.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.showtime.wallet.databinding.FragmentTransactionStatusBinding
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.vm.TransactionVModel

class TransactionStatusFragment :
    BaseSecondaryFragment<FragmentTransactionStatusBinding, TransactionVModel>() {

    private lateinit var keyType: String
    private lateinit var keyTxHash: String

    companion object {
        val TYPE_SWAP = 1
        val TYPE_SEND_TOKEN = 2
        val KEY_TYPE = "type"
        val KEY_TX_HASH = "txHash"

        fun start(context: Context, type: Int, txHash: String) {
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
            bundle.putString(KEY_TX_HASH, txHash)

            TerminalActivity.start(
                context, TerminalActivity.Companion.FragmentTypeEnum.STATUS,
                "", bundle
            )
        }
    }

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)

        keyType = extras.getString(KEY_TYPE) ?: ""
        keyTxHash = extras.getString(KEY_TX_HASH) ?: ""
    }

    override fun getContentViewLayoutID() = R.layout.fragment_transaction_status

    override fun initLiveDataObserve() {
        mViewModel.getTransaction.observeForever {
            if (null == it) {
                //retry in 2s
            } else {
                if (it.meta.err == null) {
                    FlowEventBus.with<Boolean>(EventConstants.EVENT_REFRESH_BALANCE).post(true)
                } else {
                    //failed
                }
            }
        }
    }

    override fun initRequestData() {
        mViewModel.getTransaction(keyTxHash)
    }

    override fun FragmentTransactionStatusBinding.initView() {
    }

}