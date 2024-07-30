package com.showtime.wallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.databinding.FragmentWalletHomeBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.vm.WalletHomeVModel
import org.sol4k.PublicKey

class WalletHomeFragment : BaseProjFragment<FragmentWalletHomeBinding, WalletHomeVModel>() {

    companion object {
        fun getInstance(publicKey: String): WalletHomeFragment {
            val fragment = WalletHomeFragment()
            val args = Bundle()
            args.putString("publicKey", publicKey)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var selectedPublicKey: String
    private val TAG = WalletHomeFragment::class.simpleName

    override fun getBundleExtras(extras: Bundle) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_wallet_home

    override fun FragmentWalletHomeBinding.initView() {
        selectedPublicKey = arguments?.getString("publicKey") ?: ""
        Log.e(TAG, "selectedPublicKey:${selectedPublicKey}")

        mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))

        mBinding.btnReceive.setOnClickListener {
            TerminalActivity.start(requireContext(), TerminalActivity.Companion.FragmentTypeEnum.RECEIVE, selectedPublicKey)
        }

        mBinding.btnSend.setOnClickListener {
            TerminalActivity.start(requireContext(), TerminalActivity.Companion.FragmentTypeEnum.SEARCH, selectedPublicKey)
        }

        mBinding.btnSwap.setOnClickListener {
            TerminalActivity.start(requireContext(), TerminalActivity.Companion.FragmentTypeEnum.SWAP, selectedPublicKey)
        }

        mBinding.btnNft.setOnClickListener {
            TerminalActivity.start(requireContext(), TerminalActivity.Companion.FragmentTypeEnum.NFT, selectedPublicKey)
        }

        mBinding.btnTransactions.setOnClickListener {
            TerminalActivity.start(requireContext(), TerminalActivity.Companion.FragmentTypeEnum.TRANSACTION, selectedPublicKey)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initLiveDataObserve() {
        mViewModel.getBlanceLiveData.observeForever { mBinding.tvBalance.text = "$${it} USD" }

        mViewModel.getTokens.observeForever {
            val adapter = TokenAccountsByOwnerAdapter(requireActivity(), it, false, "", selectedPublicKey)
            mBinding.rvTokenList.adapter = adapter

            mViewModel.getBalance(it)
        }
        mViewModel.getBlanceTotal.observeForever {
            mBinding.tvBalance.text = it
        }

        FlowEventBus.with<Boolean>(EventConstants.EVENT_REFRESH_BALANCE)
            .register(requireActivity()) {
                mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))
            }
    }

    override fun initRequestData() {
    }

}