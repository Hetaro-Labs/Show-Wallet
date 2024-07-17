package com.showtime.wallet

import android.os.Bundle
import android.util.Log
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.showtime.wallet.databinding.FragmentNftListBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.vm.NftVModel
import org.sol4k.PublicKey


//TODO use arguments to pass key, like @WalletHomeFragment
class NFTFragment(val key:String): BaseProjFragment<FragmentNftListBinding,NftVModel>(){

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_nft_list

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
        //TODO implement with new API, see scripts/getNFTListViaHelius.py
    }

    override fun FragmentNftListBinding.initView() {
    }
}
