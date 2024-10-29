package com.showtime.wallet.demo

import android.content.Context
import com.showtime.wallet.BaseSecondaryNotMVVMFragment
import com.showtime.wallet.DefaultTokenListData
import com.showtime.wallet.R
import com.showtime.wallet.SendTokenConfirmationFragment
import com.showtime.wallet.TerminalActivity
import com.showtime.wallet.databinding.FragmentDemosBinding

class DemoFragment: BaseSecondaryNotMVVMFragment<FragmentDemosBinding>() {

    companion object {
        fun start(context: Context, key: String) {
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.DEMO,
                key
            )
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_demos
    }

    override fun FragmentDemosBinding.initView() {
        mBinding.btnRunningApp.setOnClickListener {
            RunningAppFragment.start(requireContext(), key)
        }

        mBinding.btnPriceAlert.setOnClickListener {
            TokenPriceChangeFragment.start(requireContext(), key)
        }

        mBinding.btnTransaction.setOnClickListener {
            SendTokenConfirmationFragment.start(
                requireContext(),
                key,
                DefaultTokenListData.USDC,
                "HDNHxCnmeTvAwhrie5kWNzvNKmJPkohzsi5imdu2z6Xe",
                2.1
            )
        }

    }
}