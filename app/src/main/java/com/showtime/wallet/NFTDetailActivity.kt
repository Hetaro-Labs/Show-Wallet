package com.showtime.wallet

import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.bean.GetAssetsByOwnerResultItem
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.adapter.NFTAttributeAdapter
import com.showtime.wallet.databinding.ActivityNftDetailBinding
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.vm.NFTVModel

class NFTDetailActivity : BaseProjActivity<ActivityNftDetailBinding,NFTVModel>(){

    private lateinit var data: GetAssetsByOwnerResultItem

    override fun getBundleExtras(extras: Bundle?) {
        data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(AppConstants.KEY, GetAssetsByOwnerResultItem::class.java)!!
        else
            intent.getParcelableExtra(AppConstants.KEY)!!
    }
    override fun getContentViewLayoutID() = R.layout.activity_nft_detail

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
    }

    override fun ActivityNftDetailBinding.initView() {
        data.let {
            mBinding.nftName.text=it.content.metadata.name
            ImageHelper.obtainImage(this@NFTDetailActivity,it.content.files[0].cdn_uri,mBinding.nftImage)
            mBinding.descriptionText.text=it.content.metadata.description
            mBinding.descriptionLink.text=it.content.links.external_url
            val adapter=NFTAttributeAdapter(this@NFTDetailActivity,it.content.metadata.attributes)
            mBinding.rvAttributes.adapter=adapter
        }
    }
}