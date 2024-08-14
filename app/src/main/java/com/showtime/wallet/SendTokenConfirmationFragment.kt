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
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    BaseSecondaryNotMVVMFragment<FragmentSendConfirmationBinding>() {

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

    private lateinit var myAccount: Keypair
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

        GlobalScope.launch(Dispatchers.IO) {
            Ed25519KeyRepositoryNew.getByPublicKey(
                MmkvUtils.getString(AppConstants.SELECTED_PUBLIC_KEY) ?: ""
            )?.let {
                myAccount = it
            }
        }
    }

    override fun FragmentSendConfirmationBinding.initView() {
        tvAmount.text = uiAmount.toString() + " " + token.symbol
        tvInfoToAddress.text = receiver

        confirmButton.clickNoRepeat {
            confirmButton.isEnabled = false
            confirmButtonLabel.setText(R.string.sending)
            confirmButtonProgress.visible()

            if (token.mint == DefaultTokenListData.SOL.mint) {
                sendSolToken(
                    PublicKey(receiver),
                    (uiAmount * 10.0.pow(token.decimals.toDouble())).toLong()
                )
            } else {
                sendSPLToken(
                    myAccount,
                    PublicKey(token.mint),
                    PublicKey(receiver),
                    (uiAmount * 10.0.pow(token.decimals.toDouble())).toLong()
                )
            }
        }
    }

    private fun sendSolToken(receiver: PublicKey, amount: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            val sender = myAccount
            log(
                "send SOL: ${sender.publicKey.toBase58()}, \n" +
                        "${receiver.toBase58()}, \n"
            )

            val connection = Connection(QuickNodeUrl.MAINNNET)
            val blockhash = connection.getLatestBlockhash()
            val instruction =
                TransferInstruction(sender.publicKey, receiver, lamports = amount)
            val transaction =
                Transaction(blockhash, instruction, feePayer = sender.publicKey)
            transaction.sign(sender)

            try {
                val signature = connection.sendTransaction(transaction)
                log("sending SPL: $signature")

                TransactionStatusFragment.start(
                    requireContext(),
                    assembleMessage(),
                    signature
                )
                requireActivity().finish()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendSPLToken(
        sender: Keypair,
        tokenMintAddress: PublicKey,
        receiverAddress: PublicKey,
        amount: Long
    ) {
        object : Thread() {
            override fun run() {
                val connection = Connection(QuickNodeUrl.MAINNNET)
                val blockhash = connection.getLatestBlockhash()

                val instructions = ArrayList<Instruction>()

                val fromAssociatedTokenAddress =
                    PublicKey.findProgramDerivedAddress(sender.publicKey, tokenMintAddress)
                val toAssociatedTokenAddress =
                    PublicKey.findProgramDerivedAddress(receiverAddress, tokenMintAddress)

                val destination = connection.getAccountInfo(toAssociatedTokenAddress.publicKey)
                if (destination == null) {
                    instructions.add(
                        CreateAssociatedTokenAccountInstruction(
                            sender.publicKey,
                            toAssociatedTokenAddress.publicKey,
                            receiverAddress,
                            tokenMintAddress
                        )
                    )
                }

                log(
                    "send SPL: ${sender.publicKey.toBase58()}, \n" +
                            "${fromAssociatedTokenAddress.publicKey.toBase58()}, \n" +
                            "${toAssociatedTokenAddress.publicKey.toBase58()}, \n" +
                            "${receiverAddress.toBase58()}, \n" +
                            "${tokenMintAddress.toBase58()}, \n"
                )

                instructions.add(
                    SplTransferInstruction(
                        fromAssociatedTokenAddress.publicKey,
                        toAssociatedTokenAddress.publicKey,
                        sender.publicKey,
                        amount
                    )
                )

                val transaction = Transaction(blockhash, instructions, feePayer = sender.publicKey)
                transaction.sign(sender)

                try {
                    val signature = connection.sendTransaction(transaction)
                    TransactionStatusFragment.start(
                        requireContext(),
                        assembleMessage(),
                        signature
                    )
                    requireActivity().finish()

                    log("sending SPL: $signature")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()

    }

    private fun assembleMessage(): String{
        return getString(R.string.sending_token_to, uiAmount.toString() + token.symbol, receiver)
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_send_confirmation
    }

}