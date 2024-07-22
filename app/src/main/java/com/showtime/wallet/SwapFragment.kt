package com.showtime.wallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.ui.BaseProjFragment
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.databinding.FragmentSwapBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.utils.addTextChangeListener
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.SwapVModel
import org.sol4k.Keypair
import kotlin.math.pow

class SwapFragment(val key:String) : BaseProjFragment<FragmentSwapBinding,SwapVModel>(){

    private lateinit var myAccount: Keypair
    private var respData: TokenPairUpdatedResp? = null
    private var price: Double? = null
    private var token1: Token? = null
    private var token2: Token? = null

    override fun getBundleExtras(extras: Bundle?) {
    }

    override fun getContentViewLayoutID() = R.layout.fragment_swap

    @SuppressLint("SetTextI18n")
    override fun initLiveDataObserve() {

        FlowEventBus.with<Token>(EventConstants.EVENT_SEL_TOKEN)
            .register(requireActivity()) {
                when(it.tokenType){
                    SwapVModel.TokenTypeEnum.TOKEN1.value->{
                        token1=it
                        onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN1,token1)
                    }
                    SwapVModel.TokenTypeEnum.TOKEN2.value->{
                        token2=it
                        onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN2,token2)
                    }
                }
            }

        mViewModel.getTokenPairUpdated.observeForever {
            respData=it
            mBinding.coinAmount2.setText(it.outAmount!!.toString())
            price = it.inAmount?.toDouble()!! / it.outAmount?.toDouble()!!
            mBinding.lowestPriceInfo.text = "1 ${token1!!.symbol} = price ${token2!!.symbol}"
            var providerName=""
            for (plan in it.routePlan?: arrayListOf())
                providerName=providerName+plan.swapInfo?.label + "+"
            mBinding.providerName.text = providerName
            mBinding.btnReviewSwap.isEnabled = true
        }
    }

    override fun initRequestData() {
    }

    override fun FragmentSwapBinding.initView() {
        btnSwapToken1.setOnClickListener {
            selectToken(SwapVModel.TokenTypeEnum.TOKEN1)
        }
        btnSwapToken2.setOnClickListener {
            selectToken(SwapVModel.TokenTypeEnum.TOKEN2)
        }
        btnSelectToken.setOnClickListener {
            selectToken(SwapVModel.TokenTypeEnum.TOKEN2)
        }
        btnSwitchTokens.setOnClickListener {
            //switch token1 and token2
        }
        coinAmount1.addTextChangeListener{
            if (price == null){
                onTokenPairUpdated()
            }else{
                //TODO update coin_amount_2 according to price
            }

            //TODO check if coin_amount_1 <= token1 balance
        }
        btnReviewSwap.setOnClickListener{
            swap()
        }

        token1=DefaultTokenListData.SOL
        onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN1, DefaultTokenListData.SOL)
    }

    private fun selectToken(tokenTypeEnum:SwapVModel.TokenTypeEnum) {
        price = null
        respData = null
        val bundle=Bundle()
        bundle.putBoolean(AppConstants.FROM_SWAP_TAG,true)
        bundle.putString(AppConstants.FROM_SWAP_TOKENTYPE,tokenTypeEnum.value)
        openActivity(SearchTokenActivity::class.java,bundle)
    }

    private fun onTokenSelected(tokenTypeEnum:SwapVModel.TokenTypeEnum, token: Token?) {
        //TODO update UI
        if (SwapVModel.TokenTypeEnum.TOKEN2 == tokenTypeEnum){
            mBinding.btnSwitchTokens.visible()
            mBinding.token2Layout.visible()
            mBinding.coinName2.text=token?.tokenName
            mBinding.coinBalance2.text=token?.uiAmount.toString()
            ImageHelper.obtainImage(requireActivity(),token?.logo?:"",mBinding.coinIcon2)
        }else{
            mBinding.coinName1.text=token?.tokenName
            mBinding.coinBalance1.text=token?.uiAmount.toString()
            ImageHelper.obtainImage(requireActivity(),token?.logo?:"",mBinding.coinIcon1)
        }
        onTokenPairUpdated()
    }

    private fun onTokenPairUpdated() {
        mBinding.btnReviewSwap.isEnabled = false
        if(mBinding.coinAmount1.text.toString().isBlank()) return
        if (token1 == null || token2 == null) return

        val amount = mBinding.coinAmount1.text.toString().toDouble()

        val parameter1=token1!!.mint
        val parameter2=token2!!.mint
        val parameter3=amount * (10.0.pow(token1!!.decimals.toDouble()))
        mViewModel.getTokenPairUpdated(parameter1,parameter2,parameter3.toInt())
    }

    private fun swap() {
        if (token1 == null || token2 == null || respData == null) return

        val intent= Intent(requireActivity(),SwapConfirmActivity::class.java)
        intent.putExtra(AppConstants.SELECTED_PUBLIC_KEY,key)
        intent.putExtra(AppConstants.KEY,token1)
        intent.putExtra(AppConstants.KEY2,token2)
        intent.putExtra(AppConstants.KEY3,respData)
        startActivity(intent)
    }

}