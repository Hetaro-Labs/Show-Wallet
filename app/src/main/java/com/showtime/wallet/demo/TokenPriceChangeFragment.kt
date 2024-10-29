package com.showtime.wallet.demo

import android.content.Context
import com.showtime.wallet.BaseSecondaryFragment
import com.showtime.wallet.BaseSecondaryNotMVVMFragment
import com.showtime.wallet.R
import com.showtime.wallet.TerminalActivity
import com.showtime.wallet.databinding.FragmentTokenPriceChangedBinding

class TokenPriceChangeFragment: BaseSecondaryNotMVVMFragment<FragmentTokenPriceChangedBinding>() {

    companion object {
        fun start(context: Context, key: String) {
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.TOKEN_PRICE_ALERT,
                key
            )
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_token_price_changed
    }

    override fun FragmentTokenPriceChangedBinding.initView() {
        mBinding.btnActivateAi.setOnClickListener {
            AssistanceFragment.start(requireContext(), key)
        }
    }

}