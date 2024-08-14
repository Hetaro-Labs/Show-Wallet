package com.showtime.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.showtime.wallet.databinding.FragmentTransactionStatusBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.TransactionVModel

class TransactionStatusFragment :
    BaseSecondaryFragment<FragmentTransactionStatusBinding, TransactionVModel>() {

    private var keyType: Int = 0
    private lateinit var keyTxHash: String
    private lateinit var message: String

    companion object {
        val TYPE_SWAP = 1
        val TYPE_SEND_TOKEN = 2
        val KEY_TYPE = "type"
        val KEY_TX_HASH = "txHash"
        val KEY_MESSAGE = "message"
        val KEY_TRANSACTION = "transaction"

        fun start(context: Context, message: String, txHash: String) {
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, TYPE_SEND_TOKEN)
            bundle.putString(KEY_TX_HASH, txHash)
            bundle.putString(KEY_MESSAGE, message)

            TerminalActivity.start(
                context, TerminalActivity.Companion.FragmentTypeEnum.STATUS,
                "", bundle
            )
        }
    }

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)

        keyType = extras.getInt(KEY_TYPE) ?: 0
        keyTxHash = extras.getString(KEY_TX_HASH) ?: ""
        message = extras.getString(KEY_MESSAGE) ?: ""
    }

    override fun getContentViewLayoutID() = R.layout.fragment_transaction_status

    override fun initLiveDataObserve() {
        mViewModel.getTransaction.observeForever {
            if (null == it) {
                //retry in 2s
                Handler().postDelayed({
                    mViewModel.getTransaction(keyTxHash)
                }, 2000L)
            } else {
                if (it.meta.err == null) {
                    FlowEventBus.with<Boolean>(EventConstants.EVENT_REFRESH_BALANCE).post(true)
                    mBinding.statusIcon.setImageResource(R.drawable.ic_status_success)
                    mBinding.statusProgress.gone()
                    mBinding.statusBody.gone()
                    mBinding.statusSucceed.visible()
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
        mBinding.statusBody.text = message
    }

}