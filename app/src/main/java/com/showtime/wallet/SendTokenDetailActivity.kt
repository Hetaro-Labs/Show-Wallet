package com.showtime.wallet

import android.os.Build
import android.os.Bundle
import com.amez.mall.lib_base.ui.BaseProjNotMVVMActivity
import com.showtime.wallet.databinding.ActivitySendTokenDetailBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.instruction.CreateAssociatedTokenAccountInstruction
import org.sol4k.instruction.Instruction
import org.sol4k.instruction.SplTransferInstruction
import org.sol4k.instruction.TransferInstruction

class SendTokenDetailActivity : BaseProjNotMVVMActivity<ActivitySendTokenDetailBinding>() {

    private lateinit var myAccount: Keypair
    private lateinit var token: Token


    override fun getBundleExtras(extras: Bundle?) {
        token = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(AppConstants.KEY,Token::class.java)!!
        else
            intent.getParcelableExtra(AppConstants.KEY)!!
    }

    override fun getContentViewLayoutID() = R.layout.activity_send_token_detail

    override fun ActivitySendTokenDetailBinding.initView() {
        nextButton.clickNoRepeat {
            if (token.mint.isEmpty()) {
                sendSolToken(
                    PublicKey(receiverAddress.text.toString()),
                    (amount.text.toString().toInt()) * Math.pow(10.0, token.decimals).toLong()
                )
            } else {
                sendSPLToken(
                    myAccount,
                    PublicKey(token.mint),
                    PublicKey(receiverAddress.text.toString()),
                    (amount.text.toString().toInt()) * Math.pow(10.0, token.decimals).toLong()
                )
            }
        }
        cancelButton.clickNoRepeat { finish() }
    }


    private fun sendSolToken(receiver: PublicKey, amount: Long) {
        val sender = myAccount
        val connection = Connection(RpcUrl.DEVNET)
        val blockhash = connection.getLatestBlockhash()
        val instruction =
            TransferInstruction(sender.publicKey, receiver, lamports = amount)
        val transaction =
            Transaction(blockhash, instruction, feePayer = sender.publicKey)
        transaction.sign(sender)

        try {
            val signature = connection.sendTransaction(transaction)
        } catch (e: Exception) {
        }
    }

    private fun sendSPLToken(
        sender: Keypair,
        tokenMintAddress: PublicKey,
        receiverAddress: PublicKey,
        amount: Long
    ) {
        val connection = Connection(RpcUrl.DEVNET)
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
        } catch (e: Exception) {
        }
    }
}