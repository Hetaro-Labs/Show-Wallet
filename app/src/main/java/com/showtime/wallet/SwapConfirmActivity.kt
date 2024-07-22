package com.showtime.wallet

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.ui.BaseProjActivity
import com.amez.mall.lib_base.utils.ImageHelper
import com.google.gson.Gson
import com.showtime.wallet.databinding.ActivityConfirmSwapBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.SwapVModel

class SwapConfirmActivity : BaseProjActivity<ActivityConfirmSwapBinding,SwapVModel>(){

    private val TAG = SwapConfirmActivity::class.simpleName
    private lateinit var token: Token
    private lateinit var token2: Token
    private lateinit var quoteResp: TokenPairUpdatedResp
    private var publicKey:String?=null

    override fun getBundleExtras(extras: Bundle?) {
        token = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(AppConstants.KEY,Token::class.java)!!
        else
            intent.getParcelableExtra(AppConstants.KEY)!!

        token2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(AppConstants.KEY2,Token::class.java)!!
        else
            intent.getParcelableExtra(AppConstants.KEY2)!!

        quoteResp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(AppConstants.KEY3,TokenPairUpdatedResp::class.java)!!
        else
            intent.getParcelableExtra(AppConstants.KEY3)!!
        publicKey=intent.getStringExtra(AppConstants.SELECTED_PUBLIC_KEY)
        Log.d(TAG,"token==${token}")
        Log.d(TAG,"token2==${token2}")
        Log.d(TAG,"quoteResp==${quoteResp}")
        Log.d(TAG,"publicKey==${publicKey}")
    }

    override fun getContentViewLayoutID() = R.layout.activity_confirm_swap

    override fun initLiveDataObserve() {
    }

    override fun initRequestData() {
    }

    @SuppressLint("SetTextI18n")
    override fun ActivityConfirmSwapBinding.initView() {
        token.let {
            //Payment Section
            ImageHelper.obtainImage(this@SwapConfirmActivity,it.logo,mBinding.paymentIcon)
            mBinding.paymentLabel.text=""
            mBinding.paymentAmount.text="${it.uiAmount} ${it.symbol}"
            mBinding.paymentValue.text=""
        }
        token2.let {
            //Exchange Section
            ImageHelper.obtainImage(this@SwapConfirmActivity,it.logo,mBinding.exchangeIcon)
            mBinding.exchangeLabel.text=""
            mBinding.exchangeAmount.text="${it.uiAmount} ${it.symbol}"
            mBinding.exchangeValue.text=""
        }
        swapButton.clickNoRepeat { mViewModel.doSwap(publicKey?:"",quoteResp) }
    }
}