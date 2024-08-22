package com.showtime.wallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.HomeButtonAdapter
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.FragmentWalletHomeBinding
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.utils.SwapTest
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.vm.WalletHomeVModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                    GlobalScope.launch(Dispatchers.IO) {
                        SwapTest.runTest(
                            Ed25519KeyRepositoryNew.getByPublicKey(
                                selectedPublicKey
                            )
                        )
                    }
                },
            )
        )

        mBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.getTokenAccountsByOwner(PublicKey(selectedPublicKey ?: ""))
        }
        mBinding.swipeRefresh.isRefreshing = true

        val adapter = TokenAccountsByOwnerAdapter(
            requireActivity(),
            TokenListCache.getList(),
            false,
            "",
            selectedPublicKey
        )
        mBinding.rvTokenList.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun initLiveDataObserve() {
        mViewModel.getBlanceLiveData.observeForever { mBinding.tvBalance.text = "$${it} USD" }

        mViewModel.getTokens.observeForever {
            mBinding.swipeRefresh.isRefreshing = false

            val adapter =
                TokenAccountsByOwnerAdapter(requireActivity(), it, false, "", selectedPublicKey)
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