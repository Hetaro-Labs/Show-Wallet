package com.showtime.wallet

import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.adapter.NFTAdapter
import com.showtime.wallet.databinding.FragmentNftListBinding
import com.showtime.wallet.vm.NFTVModel

class NFTFragment(val key:String): BaseProjFragment<FragmentNftListBinding,NFTVModel>(){

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_nft_list

    override fun initLiveDataObserve() {
        mViewModel.getAssetsByOwner.observeForever {
            val adapter=NFTAdapter(requireActivity(),it.result.items)
            mBinding.rvNft.adapter=adapter
        }
    }

    override fun initRequestData() {
        mViewModel.getAssetsByOwner(key)
    }

    override fun FragmentNftListBinding.initView() {
    }
}
