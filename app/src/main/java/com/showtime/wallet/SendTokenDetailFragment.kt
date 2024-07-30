package com.showtime.wallet

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.databinding.FragmentSendTokenDetailBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.TokenListCache
import com.showtime.wallet.utils.addTextChangeListener
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.gone
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.SwapVModel

class SendTokenDetailFragment :
    BaseSecondaryFragment<FragmentSendTokenDetailBinding, SwapVModel>() {

    private lateinit var token: Token
    private var toAddress: String? = null

    companion object{
        fun start(context: Context, key: String, token: Token, to: String = ""){
            val bundle = Bundle()
            bundle.putParcelable("token", token)
            bundle.putString("to", to)
            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.SEND_DETAIL,
                key,
                bundle
            )
        }
    }

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)

        token = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            extras.getParcelable("token", Token::class.java)!!
        else
            extras.getParcelable("token")!!

        toAddress = extras.getString("to")
    }

    override fun getContentViewLayoutID() = R.layout.fragment_send_token_detail

    override fun FragmentSendTokenDetailBinding.initView() {
        receiverAddress.addTextChangeListener {
            if (CryptoUtils.isValidSolanaAddress(it)) {
                addressErrorMessage.gone()
                nextButton.isEnabled = true
            } else {
                addressErrorMessage.visible()
                nextButton.isEnabled = false
            }
        }

        amount.addTextChangeListener {
            val amount = it.toDouble()
            if (amount > token.uiAmount) {
                amountErrorMessage.visible()
                nextButton.isEnabled = false
            } else {
                amountErrorMessage.gone()
                nextButton.isEnabled = true
            }
        }

        tvTitle.text = getString(R.string.send) + " " + token.symbol
        ImageHelper.obtainImage(requireContext(), token.logo, ivLogo)
        maxButton.setOnClickListener {
            amount.setText(token.uiAmount.toString())
        }

        nextButton.clickNoRepeat {
            SendTokenConfirmationFragment.start(
                requireContext(),
                key,
                token,
                receiverAddress.text.toString(),
                amount.text.toString().toDouble()
            )
        }
        cancelButton.clickNoRepeat { requireActivity().finish() }

        mViewModel.getTokenAccountBalance(token)
        toAddress?.let {
            receiverAddress.setText(it)
        }
    }

    override fun initLiveDataObserve() {
        mViewModel.getTokenAccountBalance.observeForever {
            mBinding.amountErrorMessage.gone()
            // update token balance and put in cache
            mBinding.amount.setText(it.amount)
            token.tokenAccount = it.amount
            val newTokenList = TokenListCache.getList()
            newTokenList.findLast { it.mint == token.mint }?.tokenAccount = it.amount
            TokenListCache.saveList(newTokenList)
        }
        mViewModel.getTokenAccountBalanceErr.observeForever {
            mBinding.amountErrorMessage.visible()
            mBinding.amountErrorMessage.text = it
        }
    }

    override fun initRequestData() {
    }
}