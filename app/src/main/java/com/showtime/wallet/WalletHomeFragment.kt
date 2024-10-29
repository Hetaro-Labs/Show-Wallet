package com.showtime.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.HomeButtonAdapter
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.databinding.FragmentWalletHomeBinding
import com.showtime.wallet.demo.DemoFragment
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.utils.TokenListCache
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

        mBinding.homeButtonsRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.homeButtonsRv.adapter = HomeButtonAdapter(
            requireContext(),
            listOf(
                HomeButtonAdapter.HomeButton(R.drawable.ic_receive, R.string.wallet_receive) {
                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.RECEIVE,
                        selectedPublicKey
                    )
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_scan, R.string.wallet_scan) {
                    ScanQRFragment.start(requireContext(), selectedPublicKey)
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_send, R.string.wallet_send) {
                    SearchTokenFragment.start(requireContext(), selectedPublicKey)
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_swap, R.string.wallet_swap) {
                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.SWAP,
                        selectedPublicKey
                    )
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_nft, R.string.wallet_nfts) {
                    NFTFragment.start(requireContext(), selectedPublicKey)
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_history, R.string.wallet_transactions) {
                    TerminalActivity.start(
                        requireContext(),
                        TerminalActivity.Companion.FragmentTypeEnum.TRANSACTION,
                        selectedPublicKey
                    )
                },
                HomeButtonAdapter.HomeButton(R.drawable.ic_status_success, R.string.test) {
                    DemoFragment.start(requireContext(), selectedPublicKey)
                },
            )
        )

        mBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))
        }
        mBinding.swipeRefresh.isRefreshing = true

        val adapter = TokenAccountsByOwnerAdapter(
            requireActivity(),
            TokenListCache.getList().toMutableList(),
            false,
            "",
            selectedPublicKey
        )
        mBinding.rvTokenList.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun initLiveDataObserve() {
        mViewModel.getBlanceLiveData.observe(viewLifecycleOwner){ mBinding.tvBalance.text = "$${it} USD" }

        mViewModel.getTokens.observe(viewLifecycleOwner){
            mBinding.swipeRefresh.isRefreshing = false

            val adapter =
                TokenAccountsByOwnerAdapter(requireActivity(), it.toMutableList(), false, "", selectedPublicKey)
            mBinding.rvTokenList.adapter = adapter

            mViewModel.getTokenPrices(it)
        }

        mViewModel.getTokensPrice.observe(viewLifecycleOwner){
            //issue: invoked only once? why?
            log("getTokensPrice: ${it.symbol} -> ${it.amountInUsd}")
            val adapter = mBinding.rvTokenList.adapter as TokenAccountsByOwnerAdapter
            val index = adapter.mList.indexOf(it)
            if (index >= 0){
                adapter.mList[index] = it
                adapter.notifyItemChanged(index)
            }
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