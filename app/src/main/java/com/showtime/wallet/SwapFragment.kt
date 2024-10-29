package com.showtime.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
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
    private var price1: Double? = null
    private var price2: Double? = null
    private var token1: Token? = null
    private var token2: Token? = null

    private var inputInAmount = true
    private var onInputValueChanged = false

    companion object {
        fun start(
            context: Context,
            key: String,
            token1: Token,
            token2: Token,
            inAmount: Double?,
            outAmount: Double?
        ) {
            val bundle = Bundle()
            bundle.putParcelable("token1", token1)
            bundle.putParcelable("token2", token2)
            inAmount?.let {
                bundle.putDouble("inAmount", it)
            }
            outAmount?.let {
                bundle.putDouble("outAmount", it)
            }
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.SWAP,
                key,
                bundle
            )
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

                onTokenPairUpdated()
            }

        mViewModel.getTokenPairUpdated.observe(viewLifecycleOwner) {
            price = (it.outAmount?.toDouble()!! / 10.0.pow(token2!!.decimals)) /
                    (it.inAmount?.toDouble()!! / 10.0.pow(token1!!.decimals))

            mBinding.lowestPriceValue.text = "1 ${token1!!.symbol} ≈ $price ${token2!!.symbol}"
            var providerName = ""
            for (plan in it.routePlan ?: arrayListOf())
                providerName = providerName + plan.swapInfo?.label + "+"
            mBinding.providerName.text = providerName.substring(0, providerName.length - 1)

            mBinding.layoutLlmInfo.visible()

            val inAmount = mBinding.coinAmount1.text.toString().let {
                if (it.isEmpty()) 0.0 else it.toDouble()
            }

            if (inAmount > 0) {
                mBinding.btnReviewSwap.isEnabled = true
            }

            if (inputInAmount) {
                updateOutAmount(inAmount)
            } else {
                val outAmount = mBinding.coinAmount2.text.toString().let {
                    if (it.isEmpty()) 0.0 else it.toDouble()
                }
                updateInAmount(outAmount)
            }
        }

        mViewModel.getToken1Price.observe(viewLifecycleOwner) {
            price1 = it
            mBinding.amount1Price.text =
                "$" + String.format("%.2f", getAmount(mBinding.coinAmount1) * it)
        }

        mViewModel.getToken2Price.observe(viewLifecycleOwner) {
            price2 = it
            mBinding.amount2Price.text =
                "$" + String.format("%.2f", getAmount(mBinding.coinAmount2) * it)
        }
    }

    override fun initRequestData() {
    }

    private fun insufficientBalance() {
        mBinding.amount1Price.gone()
        mBinding.btnReviewSwap.isEnabled = false
        mBinding.amount1ErrorMessage.visible()
    }

    private fun sufficientBalance() {
        mBinding.amount1Price.visible()
        mBinding.btnReviewSwap.isEnabled = true
        mBinding.amount1ErrorMessage.gone()
    }

    private fun getAmount(editText: EditText) =
        editText.text.toString().let {
            if (it.isEmpty()) 0.0 else it.toDouble()
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
            val oldToken1 = token1?.copy()

            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN1, token2)
            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN2, oldToken1)

            //update amount
            val amount1 = coinAmount1.text
            val amount2 = coinAmount2.text

            coinAmount1.text = amount2
            coinAmount2.text = amount1

            onTokenPairUpdated()
        }

        coinAmount2.addTextChangeListener {
            if (onInputValueChanged) {
                onInputValueChanged = false
                return@addTextChangeListener
            }

            val outAmount = getAmount(mBinding.coinAmount2)

            inputInAmount = false
            if (price == null) {
                onTokenPairUpdated()
            } else {
                updateInAmount(outAmount)
            }

            if (price2 == null) {
                mViewModel.getToken1PriceUpdated(token2!!)
            } else {
                mBinding.amount2Price.text = "$" + String.format("%.2f", outAmount * price2!!)
            }
        }

        coinAmount1.addTextChangeListener {
            if (onInputValueChanged) {
                onInputValueChanged = false
                return@addTextChangeListener
            }

            val inAmount = getAmount(mBinding.coinAmount1)

            inputInAmount = true

            if (inAmount <= 0.0) {
                mBinding.btnReviewSwap.isEnabled = false
            } else {
                if (token1!!.uiAmount >= inAmount) {
                    sufficientBalance()
                } else {
                    insufficientBalance()
                }

                if (price == null) {
                    onTokenPairUpdated()
                } else {
                    updateOutAmount(inAmount)
                }
            }

            if (price1 == null) {
                mViewModel.getToken1PriceUpdated(token1!!)
            } else {
                mBinding.amount1Price.text = "$" + String.format("%.2f", inAmount * price1!!)
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

    private fun postHandleArguments() {
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
        } else {
            val token2 = TokenListCache.getList().find { it.mint == DefaultTokenListData.USDC.mint }
                ?: DefaultTokenListData.USDC

            onTokenSelected(SwapVModel.TokenTypeEnum.TOKEN2, token2)
        }

        onTokenPairUpdated()

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
            price2 = null //TokenListCache.getPrice(token?.mint?:"")

            mBinding.btnSwitchTokens.visible()
            mBinding.token2Layout.visible()
            mBinding.btnSelectToken.gone()
            mBinding.coinName2.text = token?.symbol
//            mBinding.coinBalance2.text = token?.uiAmount.toString()
            ImageHelper.obtainImage(requireActivity(), token?.logo ?: "", mBinding.coinIcon2)
        } else {
            token1 = token
            price1 = null //TokenListCache.getPrice(token?.mint?:"")

            mBinding.coinName1.text = token?.symbol
            mBinding.coinBalance1.text = token?.uiAmount.toString()
            ImageHelper.obtainImage(requireActivity(), token?.logo ?: "", mBinding.coinIcon1)
        }
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

        if (price1 != null) {
            mBinding.amount1Price.text = "$" + String.format("%.2f", inAmount * price1!!)
        } else {
            mViewModel.getToken1PriceUpdated(token1!!)
        }
    }

    private fun updateOutAmount(inAmount: Double) {
        val outAmount = inAmount * price!!
        onInputValueChanged = true
        mBinding.coinAmount2.setText(outAmount.toString())

        if (price2 != null) {
            mBinding.amount2Price.text = "$" + String.format("%.2f", outAmount * price2!!)
        } else {
            mViewModel.getToken2PriceUpdated(token2!!)
        }
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

        token1!!.amountInUsd = mBinding.amount1Price.text.toString().substring(1).toDouble()
        token2!!.amountInUsd = mBinding.amount2Price.text.toString().substring(1).toDouble()

        TerminalActivity.start(
            requireContext(),
            TerminalActivity.Companion.FragmentTypeEnum.SWAP_CONFIRMATION,
            key,
            SwapConfirmFragment.getBundle(
                token1!!,
                token2!!
            )
        )

        requireActivity().finish()
    }

}