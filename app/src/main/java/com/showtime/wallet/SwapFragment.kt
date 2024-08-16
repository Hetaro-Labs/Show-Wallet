package com.showtime.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.bus.FlowEventBus
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.databinding.FragmentSwapBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.EventConstants
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.addTextChangeListener
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.SwapVModel
import java.math.BigInteger
import kotlin.math.pow

class SwapFragment : BaseSecondaryFragment<FragmentSwapBinding, SwapVModel>() {

//    private var respData: TokenPairUpdatedResp? = null
    private var price: Double? = null
    private var token1: Token? = null
    private var token2: Token? = null

    private var inputInAmount = true
    private var onInputValueChanged = false

    companion object {
        fun start(context: Context, key: String, token1: Token, token2: Token, inAmount: Double) {
            val bundle = Bundle()
            bundle.putParcelable("token1", token1)
            bundle.putParcelable("token2", token2)
            bundle.putDouble("inAmount", inAmount)
            TerminalActivity.start(context, TerminalActivity.Companion.FragmentTypeEnum.SWAP, key, bundle)
        }
    }

    override fun getContentViewLayoutID() = R.layout.fragment_swap

    @SuppressLint("SetTextI18n")
    override fun initLiveDataObserve() {
        FlowEventBus.with<Token>(EventConstants.EVENT_SEL_TOKEN)
            .register(requireActivity()) {
                when (it.tokenType) {
                    SwapVModel.TokenTypeEnum.TOKEN1.value -> {
                        token1 = it
                        onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN1, token1)
                    }

                    SwapVModel.TokenTypeEnum.TOKEN2.value -> {
                        token2 = it
                        onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN2, token2)
                    }
                }
            }

        mViewModel.getTokenPairUpdated.observeForever {
//            respData = it
            price = (it.outAmount?.toDouble()!! / 10.0.pow(token2!!.decimals)) /
                    (it.inAmount?.toDouble()!! / 10.0.pow(token1!!.decimals))

            mBinding.lowestPriceValue.text = "1 ${token1!!.symbol} ≈ $price ${token2!!.symbol}"
            var providerName = ""
            for (plan in it.routePlan ?: arrayListOf())
                providerName = providerName + plan.swapInfo?.label + "+"
            mBinding.providerName.text = providerName
            mBinding.btnReviewSwap.isEnabled = true

            if (inputInAmount){
                val inAmount = mBinding.coinAmount1.text.toString().let {
                    if (it.isEmpty()) 0.0 else it.toDouble()
                }
                updateOutAmount(inAmount)
            }else{
                val outAmount = mBinding.coinAmount2.text.toString().let {
                    if (it.isEmpty()) 0.0 else it.toDouble()
                }
                updateInAmount(outAmount)
            }
        }
    }

    override fun initRequestData() {
    }

    private fun insufficientBalance(){
        mBinding.btnReviewSwap.isEnabled = false
        mBinding.amount1ErrorMessage.visible()
    }

    private fun sufficientBalance(){
        mBinding.btnReviewSwap.isEnabled = true
        mBinding.amount1ErrorMessage.gone()
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
            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN1, token2)
            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN2, token1)
        }

        coinAmount2.addTextChangeListener {
            if (onInputValueChanged){
                onInputValueChanged = false
                return@addTextChangeListener
            }

            val outAmount = mBinding.coinAmount2.text.toString().let {
                if (it.isEmpty()) 0.0 else it.toDouble()
            }

            inputInAmount = false
            if (price == null) {
                onTokenPairUpdated()
            } else {
                updateInAmount(outAmount)
            }
        }

        coinAmount1.addTextChangeListener {
            if (onInputValueChanged){
                onInputValueChanged = false
                return@addTextChangeListener
            }

            val inAmount = mBinding.coinAmount1.text.toString().let {
                if (it.isEmpty()) 0.0 else it.toDouble()
            }

            inputInAmount = true
            if (token1!!.uiAmount >= inAmount) {
                sufficientBalance()

                if (price == null) {
                    onTokenPairUpdated()
                } else {
                    updateOutAmount(inAmount)
                }
            } else {
                insufficientBalance()
            }
        }

        btnReviewSwap.setOnClickListener {
            swap()
        }

        maxButton1.setOnClickListener {
            coinAmount1.setText(token1!!.uiAmount.toString())
        }

        postHandleArguments()
    }

    private fun postHandleArguments(){
        val extras = requireArguments()
        if (extras.containsKey("token1")) {
            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN1, extras.getParcelable("token1"))
        } else {
            val token1 = TokenListCache.getList().find { it.mint == DefaultTokenListData.SOL.mint }
                ?: DefaultTokenListData.SOL

            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN1, token1)
        }

        if (extras.containsKey("token2")) {
            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN2, extras.getParcelable("token2"))
        }

        if (extras.containsKey("inAmount")) {
            val inAmount = extras.getDouble("inAmount")
            mBinding.coinAmount1.setText(inAmount.toString())
        }

        if (extras.containsKey("outAmount")) {
            val outAmount = extras.getDouble("outAmount")
            mBinding.coinAmount2.setText(outAmount.toString())
        }
    }

    private fun selectToken(tokenTypeEnum: SwapVModel.TokenTypeEnum) {
        price = null
//        respData = null
        val bundle = Bundle()
        bundle.putBoolean(AppConstants.FROM_SWAP_TAG, true)
        bundle.putString(AppConstants.FROM_SWAP_TOKENTYPE, tokenTypeEnum.value)

        TerminalActivity.start(
            requireContext(),
            TerminalActivity.Companion.FragmentTypeEnum.SEARCH,
            key,
            bundle
        )
    }

    private fun onTokenSelected(tokenTypeEnum: SwapVModel.TokenTypeEnum, token: Token?) {
        if (SwapVModel.TokenTypeEnum.TOKEN2 == tokenTypeEnum) {
            token2 = token

            mBinding.btnSwitchTokens.visible()
            mBinding.token2Layout.visible()
            mBinding.btnSelectToken.gone()
            mBinding.coinName2.text = token?.tokenName
            mBinding.coinBalance2.text = token?.uiAmount.toString()
            ImageHelper.obtainImage(requireActivity(), token?.logo ?: "", mBinding.coinIcon2)
        } else {
            token1 = token

            mBinding.coinName1.text = token?.tokenName
            mBinding.coinBalance1.text = token?.uiAmount.toString()
            ImageHelper.obtainImage(requireActivity(), token?.logo ?: "", mBinding.coinIcon1)
        }

        onTokenPairUpdated()
    }

    private fun updateInAmount(outAmount: Double) {
        onInputValueChanged = true
        val inAmount = outAmount / price!!
        mBinding.coinAmount1.setText(inAmount.toString())

        if (token1!!.uiAmount >= inAmount) {
            sufficientBalance()
        } else {
            insufficientBalance()
        }
    }

    private fun updateOutAmount(inAmount: Double) {
        onInputValueChanged = true
        mBinding.coinAmount2.setText((inAmount * price!!).toString())
    }

    private fun onTokenPairUpdated() {
        mBinding.btnReviewSwap.isEnabled = false
        if (token1 == null || token2 == null) return

        val mint1 = token1!!.mint
        val mint2 = token2!!.mint
        val amount = BigInteger.valueOf((10.0.pow(token1!!.decimals.toDouble())).toLong())

        //only get price
        mViewModel.getTokenPairUpdated(mint1, mint2, amount)
    }

    private fun swap() {
        if (token1 == null || token2 == null) return

        token1!!.uiAmount = mBinding.coinAmount1.text.toString().toDouble()
        token2!!.uiAmount = mBinding.coinAmount2.text.toString().toDouble()

        TerminalActivity.start(
            requireContext(),
            TerminalActivity.Companion.FragmentTypeEnum.SWAP_CONFIRMATION,
            key,
            SwapConfirmFragment.getBundle(
                token1!!,
                token2!!
            )
        )
    }

}