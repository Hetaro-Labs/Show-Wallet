package com.showtime.wallet

import org.sol4k.AccountMeta
import org.sol4k.Connection
import org.sol4k.Constants.SYSTEM_PROGRAM
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.instruction.BaseInstruction

class SwapConfirmActivity {

    /**private fun init() {
        //TODO activity_swap_confirm
        // display info, data from SwapFragment
    }

    private fun swap() {
        //TODO get data from SwapFragment

        // get serialized transactions for the swap
        const { swapTransaction } = await(
            await fetch ('https://quote-api.jup.ag/v6/swap', {
                method: 'POST',
                headers: {
                'Content-Type': 'application/json'
            },
                body: JSON.stringify({
                // quoteResponse from /quote api
                quoteResponse,
                // user public key to be used for the swap
                userPublicKey: wallet.publicKey.toString(),
                // auto wrap and unwrap SOL. default is true
                wrapAndUnwrapSol: true,
            })
            })
        ).json();

        // deserialize the transaction
        const swapTransactionBuf = Buffer . from (swapTransaction, 'base64');

        val sender = myAccount
        val connection = Connection(RpcUrl.DEVNET)
        val blockhash = connection.getLatestBlockhash()
        val instruction =
            BaseInstruction(
                swapTransactionBuf, listOf(
                    AccountMeta(myAccount.publicKey, writable = true, signer = true)
                ),
                SYSTEM_PROGRAM
            )
        val transaction =
            Transaction(blockhash, instruction, feePayer = sender.publicKey)
        transaction.sign(sender)

        try {
            val signature = connection.sendTransaction(transaction)
        } catch (e: Exception) {
        }
    }**/
}