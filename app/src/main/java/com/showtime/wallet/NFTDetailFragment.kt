package com.showtime.wallet

import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResultItem
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.adapter.NFTAttributeAdapter
import com.showtime.wallet.databinding.FragmentNftDetailBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.NFTVModel

class NFTDetailFragment : BaseSecondaryFragment<FragmentNftDetailBinding, NFTVModel>() {

    private lateinit var data: GetAssetsByOwnerResultItem

    companion object {
        fun getBundle(data: GetAssetsByOwnerResultItem): Bundle {
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
            (requireActivity() as TerminalActivity).setTitle(
                it.content.metadata.name
            )

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

        mBinding.btnSend.clickNoRepeat {
            val name = data.content.metadata.name
            val symbol = data.content.metadata.symbol
            val mint = data.id
            val logo = data.content.files[0].cdn_uri

            val token = Token(
                mint,
                name,
                symbol,
                1,
                logo,
                1.0,
                true,
                "",
                ""
            )

            SendTokenDetailFragment.start(requireContext(), key, token)
        }
    }
}