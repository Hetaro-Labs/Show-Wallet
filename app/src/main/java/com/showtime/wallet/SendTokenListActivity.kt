package com.showtime.wallet

import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.showtime.wallet.adapter.TokenAccountsByOwnerAdapter
import com.showtime.wallet.databinding.ActivitySendtokenlistBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.vm.WalletHomeVModel

class SendTokenListActivity : BaseProjActivity<ActivitySendtokenlistBinding, WalletHomeVModel>(){

    private lateinit var tokenList: List<Token>

    override fun getBundleExtras(extras: Bundle?) {
        tokenList = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableArrayListExtra(AppConstants.KEY,Token::class.java)!!
        else
            intent.getParcelableArrayListExtra(AppConstants.KEY)!!
    }

    override fun getContentViewLayoutID() = R.layout.activity_sendtokenlist

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
    }

    override fun ActivitySendtokenlistBinding.initView() {
        val adapter = TokenAccountsByOwnerAdapter(this@SendTokenListActivity, tokenList)
        mBinding.rvTokenList.adapter = adapter
        //OnClick is bound in TokenAccountsByOwnerAdapter
    }
}