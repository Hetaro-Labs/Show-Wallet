package com.showtime.wallet

import com.showtime.wallet.adapter.NFTAdapter
import com.showtime.wallet.databinding.FragmentNftListBinding
import com.showtime.wallet.vm.NFTVModel

class NFTFragment : BaseSecondaryFragment<FragmentNftListBinding, NFTVModel>() {

    override fun getContentViewLayoutID() = R.layout.fragment_nft_list

    override fun initLiveDataObserve() {
        mViewModel.getAssetsByOwner.observeForever {
            mBinding.swipeRefresh.isRefreshing = false

            val adapter = NFTAdapter(requireActivity(), it.result.items, key)
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
