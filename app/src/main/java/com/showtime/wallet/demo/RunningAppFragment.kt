package com.showtime.wallet.demo

import android.content.Context
import androidx.room.util.findColumnIndexBySuffix
import com.showtime.wallet.BaseSecondaryNotMVVMFragment
import com.showtime.wallet.R
import com.showtime.wallet.TerminalActivity
import com.showtime.wallet.databinding.FragmentStartRunningBinding

class RunningAppFragment: BaseSecondaryNotMVVMFragment<FragmentStartRunningBinding>() {

    companion object {
        fun start(context: Context, key: String) {
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.DEMO_RUNNING_APP,
                key
            )
        }
    }


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_start_running
    }

    override fun FragmentStartRunningBinding.initView() {
        mBinding.btnStart.setOnClickListener {
            SignTransactionFragment.start(requireContext(), key)
            requireActivity().finish()
        }
    }
}