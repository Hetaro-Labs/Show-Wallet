package com.showtime.wallet

import android.content.Context
import android.os.Bundle
import com.showtime.wallet.adapter.NFTAdapter
import com.showtime.wallet.databinding.FragmentNftListBinding
import com.showtime.wallet.vm.NFTVModel

class NFTFragment : BaseSecondaryFragment<FragmentNftListBinding, NFTVModel>() {

    companion object{
        fun start(context: Context, selectedPublicKey: String, to: String = ""){
            val bundle = Bundle()
            bundle.putString("to", to)

            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.NFT,
                selectedPublicKey,
                bundle
            )
        }
    }

    private var to: String = ""

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)
        to = extras.getString("to", "")
    }

    override fun getContentViewLayoutID() = R.layout.fragment_nft_list

    override fun initLiveDataObserve() {
        mViewModel.getAssetsByOwner.observeForever {
            mBinding.swipeRefresh.isRefreshing = false

            val adapter = NFTAdapter(requireActivity(), it.result.items, key, to)
            mBinding.rvNft.adapter = adapter
        }
    }

    override fun initRequestData() {
        mViewModel.getAssetsByOwner(key)
        mBinding.swipeRefresh.isRefreshing = true
    }

    override fun FragmentNftListBinding.initView() {
        mBinding.swipeRefresh.setOnRefreshListener {
            mViewModel.getAssetsByOwner(key)
        }
    }
}
