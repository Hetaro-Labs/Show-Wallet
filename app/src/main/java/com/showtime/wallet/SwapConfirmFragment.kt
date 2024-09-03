package com.showtime.wallet

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.FragmentConfirmSwapBinding
import com.showtime.wallet.net.QuickNodeUrl
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.SwapTest
import com.showtime.wallet.utils.SwapTest.Companion
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.SwapVModel
import kotlinx.coroutines.Dispatchers
import java.util.Base64
import kotlin.math.pow
import kotlin.math.sign

class SwapConfirmFragment : BaseSecondaryFragment<FragmentConfirmSwapBinding, SwapVModel>() {

    private lateinit var token1: Token
    private lateinit var token2: Token
    private var resp: TokenPairUpdatedResp? = null

    companion object {
        fun getBundle(token1: Token, token2: Token): Bundle {
            val bundle = Bundle()
            bundle.putParcelable("token1", token1)
            bundle.putParcelable("token2", token2)
            return bundle
        }
    }

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)

        token1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            extras.getParcelable("token1", Token::class.java)!!
        else
            extras.getParcelable("token1")!!

        token2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            extras.getParcelable("token2", Token::class.java)!!
        else
            extras.getParcelable("token2")!!

        log("token==${token1}")
        log("token2==${token2}")
    }

    override fun getContentViewLayoutID() = R.layout.fragment_confirm_swap

    override fun initLiveDataObserve() {
        mViewModel.getTransactionHash.observe(viewLifecycleOwner) {
            val msg =
                token1.uiAmount.toString() + " " + token1.symbol + " for " + token2.uiAmount + " " + token2.symbol
            TransactionStatusFragment.start(
                requireContext(),
                TransactionStatusFragment.TYPE_SWAP,
                msg,
                it
            )
            requireActivity().finish()
        }

        mViewModel.getTransactionError.observe(viewLifecycleOwner) {
            mBinding.errorMessage.visible()
            mBinding.errorMessage.text = it
            reEnableSwapButton()
        }

        mViewModel.getTokenPairUpdated.observe(viewLifecycleOwner) {
            mBinding.swapButton.isEnabled = true
            resp = it

            mBinding.slippage.text = it.slippageBps?.toString() + "%"

            mBinding.priceImpact.text = String.format("%.2f", (it.priceImpactPct!!.toDouble() * 100.0)) + "%"

            val uiAmount = resp!!.outAmount!!.toLong() / 10.0.pow(token2.decimals)
            mBinding.exchangeAmount.text = "${uiAmount} ${token2.symbol}"

            val price = (it.outAmount?.toDouble()!! / 10.0.pow(token2.decimals)) /
                    (it.inAmount?.toDouble()!! / 10.0.pow(token1.decimals))

            mBinding.lowestPriceValue.text = "1 ${token1.symbol} ≈ $price ${token2.symbol}"
            var providerName = ""
            for (plan in it.routePlan ?: arrayListOf())
                providerName = providerName + plan.swapInfo?.label + "+"
            mBinding.providerName.text = providerName
        }

        mViewModel.getSwapTransaction.observe(viewLifecycleOwner){
            mViewModel.signTransactionAndSubmit(it.swapTransaction)
        }
    }

    override fun initRequestData() {
        mBinding.swapButton.isEnabled = false
        mViewModel.getTokenPairUpdated(token1.mint, token2.mint, token1.getAmount())
    }

    @SuppressLint("SetTextI18n")
    override fun FragmentConfirmSwapBinding.initView() {
        token1.let {
            //Payment Section
            ImageHelper.obtainImage(requireContext(), it.logo, mBinding.paymentIcon)
            mBinding.paymentAmount.text = "${it.uiAmount} ${it.symbol}"
            mBinding.paymentValue.text = ""
        }
        token2.let {
            //Exchange Section
            ImageHelper.obtainImage(requireContext(), it.logo, mBinding.exchangeIcon)
            mBinding.exchangeAmount.text =
                getString(R.string.loading) //"${it.uiAmount} ${it.symbol}"
            mBinding.exchangeValue.text = ""
        }

        swapButton.clickNoRepeat {
            swapButton.isEnabled = false
            swapButtonLabel.setText(R.string.swaping)
            swapProgress.visible()

            mViewModel.doSwap(resp!!)
        }
    }

    private fun reEnableSwapButton() {
        mBinding.apply {
            swapButton.isEnabled = true
            swapButtonLabel.setText(R.string.swap)
            swapProgress.gone()
        }
    }
}