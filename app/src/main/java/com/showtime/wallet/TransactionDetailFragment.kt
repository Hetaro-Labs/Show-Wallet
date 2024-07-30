package com.showtime.wallet

import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.bean.TransactionsData
import com.amez.mall.lib_base.ui.BaseProjNotMVVMActivity
import com.amez.mall.lib_base.utils.DateUtils
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.databinding.FragmentTransactionDetailBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat


class TransactionDetailFragment : BaseSecondaryNotMVVMFragment<FragmentTransactionDetailBinding>() {

    private lateinit var transactionsData: TransactionsData

    companion object{
        fun getBundle(transactionsData: TransactionsData): Bundle{
            val bundle = Bundle()
            bundle.putParcelable("tx", transactionsData)
            return bundle
        }
    }

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)

        transactionsData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            extras.getParcelable("tx", TransactionsData::class.java)!!
        else
            extras.getParcelable("tx")!!
    }

    override fun getContentViewLayoutID() = R.layout.fragment_transaction_detail

    override fun FragmentTransactionDetailBinding.initView() {
        mBinding.let {
            closeButton.clickNoRepeat { requireActivity().finish() }
            titleText.text = transactionsData.showTransactionType
            if (transactionsData.showUrl.isNullOrEmpty())
                mainIcon.setImageResource(R.drawable.ic_solana)
            else
                ImageHelper.obtainImage(
                    requireContext(),
                    transactionsData.showUrl ?: "",
                    mainIcon
                )
            amountText.text = transactionsData.showAmount
            tvTime.text = DateUtils.timestampToAmericanDate(
                transactionsData.timestamp?.toLong() ?: System.currentTimeMillis()
            )
            tvStatus.text = transactionsData.status
            tvFrom.text = transactionsData.source
            //TODO NetWork、view_on_solscan I don't know which field to take (logic)
        }
    }
}