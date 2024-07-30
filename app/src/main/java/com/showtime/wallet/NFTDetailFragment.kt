package com.showtime.wallet

import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResultItem
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.adapter.NFTAttributeAdapter
import com.showtime.wallet.databinding.FragmentNftDetailBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.vm.NFTVModel

class NFTDetailFragment : BaseSecondaryFragment<FragmentNftDetailBinding, NFTVModel>() {

    private lateinit var data: GetAssetsByOwnerResultItem

    companion object {
        fun getBundle(data: GetAssetsByOwnerResultItem): Bundle{
            val bundle = Bundle()
            bundle.putParcelable("data", data)
            return bundle
        }
    }

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)

        data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            extras.getParcelable("data", GetAssetsByOwnerResultItem::class.java)!!
        else
            extras.getParcelable("data")!!
    }

    override fun getContentViewLayoutID() = R.layout.fragment_nft_detail

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
    }

    override fun FragmentNftDetailBinding.initView() {
        data.let {
            mBinding.nftName.text = it.content.metadata.name
            ImageHelper.obtainImage(
                requireContext(),
                it.content.files[0].cdn_uri,
                mBinding.nftImage
            )
            mBinding.descriptionText.text = it.content.metadata.description
            mBinding.descriptionLink.text = it.content.links.external_url
            val adapter =
                NFTAttributeAdapter(requireContext(), it.content.metadata.attributes)
            mBinding.rvAttributes.adapter = adapter
        }
    }
}