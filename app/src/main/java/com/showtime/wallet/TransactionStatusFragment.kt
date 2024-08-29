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

        fun start(context: Context, type: Int, message: String, txHash: String) {
            val bundle = Bundle()
            bundle.putInt(KEY_TYPE, type)
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
                //retry in 4s
                Handler().postDelayed({
                    mViewModel.getTransaction(keyTxHash)
                }, 4000L)
            } else {
                if (it.meta.err == null) {
                    FlowEventBus.with<Boolean>(EventConstants.EVENT_REFRESH_BALANCE).post(true)
                    mBinding.statusIcon.setImageResource(R.drawable.ic_status_success)
                    mBinding.statusProgress.gone()
                    mBinding.statusBody.text =
                        when (keyType) {
                            TYPE_SWAP -> {
                                getString(R.string.swapped) + message
                            }
                            TYPE_SEND_TOKEN -> {
                                getString(R.string.sent) + message
                            }
                            else -> {
                                message
                            }
                        }
                } else {
                    //failed
                    mBinding.statusIcon.setImageResource(R.drawable.ic_error)
                    mBinding.statusProgress.gone()
                    mBinding.statusBody.text = getString(R.string.transaction_failed)
                }
            }
        }
    }

    override fun initRequestData() {
        mViewModel.getTransaction(keyTxHash)
    }

    override fun FragmentTransactionStatusBinding.initView() {
        mBinding.statusBody.text =
            when (keyType) {
                TYPE_SWAP -> {
                    getString(R.string.swaping) + message
                }

                TYPE_SEND_TOKEN -> {
                    getString(R.string.sending) + message
                }

                else -> {
                    message
                }
            }
    }

}