package com.showtime.wallet

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.utils.MmkvUtils
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.FragmentSendConfirmationBinding
import com.showtime.wallet.net.QuickNodeUrl
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.CryptoUtils
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.visible
import com.showtime.wallet.vm.SendTokenVModel
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.instruction.CreateAssociatedTokenAccountInstruction
import org.sol4k.instruction.Instruction
import org.sol4k.instruction.SplTransferInstruction
import org.sol4k.instruction.TransferInstruction
import kotlin.math.pow

class SendTokenConfirmationFragment :
    BaseSecondaryFragment<FragmentSendConfirmationBinding, SendTokenVModel>() {

    companion object {
        private val KEY_AMOUNT = "amount"
        private val KEY_TOKEN = "token"
        private val KEY_RECEIVER = "receiver"

        fun start(context: Context, key: String, token: Token, receiver: String, uiAmount: Double) {
            val bundle = Bundle()
            bundle.putDouble(KEY_AMOUNT, uiAmount)
            bundle.putString(KEY_RECEIVER, receiver)
            bundle.putParcelable(KEY_TOKEN, token)

            TerminalActivity.start(
                context,
                TerminalActivity.Companion.FragmentTypeEnum.SEND_CONFIRMATION,
                key,
                bundle
            )
        }
    }

    private lateinit var token: Token
    private lateinit var receiver: String
    private var uiAmount: Double = 0.0

    override fun getBundleExtras(extras: Bundle) {
        super.getBundleExtras(extras)

        receiver = extras.getString(KEY_RECEIVER)!!
        token = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            extras.getParcelable(KEY_TOKEN, Token::class.java)!!
        else
            extras.getParcelable(KEY_TOKEN)!!
        uiAmount = extras.getDouble(KEY_AMOUNT)
    }

    override fun FragmentSendConfirmationBinding.initView() {
        tvAmount.text = uiAmount.toString() + " " + token.symbol
        tvInfoToAddress.text = receiver

        confirmButton.clickNoRepeat {
            confirmButton.isEnabled = false
            confirmButtonLabel.setText(R.string.sending)
            confirmButtonProgress.visible()

            if (token.mint == DefaultTokenListData.SOL.mint) {
                mViewModel.sendSolToken(
                    PublicKey(receiver),
                    (uiAmount * 10.0.pow(token.decimals.toDouble())).toLong()
                )
            } else {
                mViewModel.sendSPLToken(
                    PublicKey(token.mint),
                    PublicKey(receiver),
                    (uiAmount * 10.0.pow(token.decimals.toDouble())).toLong()
                )
            }
        }
    }

    private fun assembleMessage(): String{
        return getString(R.string.sending_token_to, uiAmount.toString() + token.symbol, CryptoUtils.getDisplayAddress(receiver))
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_send_confirmation
    }

    override fun initLiveDataObserve() {
        mViewModel.getTransactionHash.observe(viewLifecycleOwner){
            log("sending SPL: $it")

            TransactionStatusFragment.start(
                requireContext(),
                TransactionStatusFragment.TYPE_SEND_TOKEN,
                assembleMessage(),
                it
            )
            requireActivity().finish()
        }
    }

    override fun initRequestData() {
    }

}