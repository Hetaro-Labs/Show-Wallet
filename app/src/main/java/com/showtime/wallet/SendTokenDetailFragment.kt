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
import com.showtime.wallet.vm.SendTokenVModel
import com.showtime.wallet.vm.SwapVModel

class SendTokenDetailFragment :
    BaseSecondaryFragment<FragmentSendTokenDetailBinding, SendTokenVModel>() {

    private lateinit var token: Token
    private var toAddress: String? = null

    companion object {
        fun start(context: Context, key: String, token: Token, to: String = "") {
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
            mViewModel.onAddressChange(it)
        }

        amount.addTextChangeListener {
            mViewModel.onAmountChange(token, it)
        }

        (activity as TerminalActivity).setTitle(getString(R.string.send) + " " + token.symbol)
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
            requireActivity().finish()
        }
        cancelButton.clickNoRepeat { requireActivity().finish() }

        mViewModel.getTokenAccountBalance(token)
        toAddress?.let {
            receiverAddress.setText(it)
        }
    }

    override fun initLiveDataObserve() {
        mViewModel.getTokenAccountBalance.observe(viewLifecycleOwner){
            mBinding.amountErrorMessage.gone()
            // update token balance and put in cache
            mBinding.amount.setText(it.amount)
            token.tokenAccount = it.amount
            val newTokenList = TokenListCache.getList()
            newTokenList.findLast { it.mint == token.mint }?.tokenAccount = it.amount
            TokenListCache.saveList(newTokenList)
        }

        mViewModel.getEnterAmountErr.observe(viewLifecycleOwner){ enterAmountErr ->
            updateButtonState(enterAmountErr, mViewModel.getAddressErr.value ?: true)
            if (enterAmountErr) {
                mBinding.amountErrorMessage.visible()
            } else {
                mBinding.amountErrorMessage.gone()
            }
        }

        mViewModel.getAddressErr.observe(viewLifecycleOwner){ addressErr ->
            updateButtonState(mViewModel.getEnterAmountErr.value ?: true, addressErr)
            if (addressErr) {
                mBinding.addressErrorMessage.visible()
            } else {
                mBinding.addressErrorMessage.gone()
            }
        }
    }

    private fun updateButtonState(enterAmountErr: Boolean, addressErr: Boolean) {
        mBinding.nextButton.isEnabled = !enterAmountErr && !addressErr
    }

    override fun initRequestData() {
    }
}