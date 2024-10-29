package com.showtime.wallet.demo

import android.content.Context
import com.showtime.wallet.BaseSecondaryNotMVVMFragment
import com.showtime.wallet.R
import com.showtime.wallet.TerminalActivity
import com.showtime.wallet.TransactionStatusFragment
import com.showtime.wallet.databinding.FragmentSignTransactionBinding

class SignTransactionFragment: BaseSecondaryNotMVVMFragment<FragmentSignTransactionBinding>() {

    companion object {
        fun start(context: Context, key: String) {
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.DEMO_SIGN_TRANSACTION,
                key
            )
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_sign_transaction
    }

    override fun FragmentSignTransactionBinding.initView() {
        mBinding.btnConfirm.setOnClickListener {
            TransactionStatusFragment
                .start(
                    requireContext(), TransactionStatusFragment.TYPE_TRANSACTION,
                    "confirming transaction", ""
                )
            requireActivity().finish()
        }
    }

}