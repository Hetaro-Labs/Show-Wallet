package com.showtime.wallet

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import com.amez.mall.lib_base.bean.TokenPairUpdatedResp
import com.amez.mall.lib_base.utils.ImageHelper
import com.showtime.wallet.data.Ed25519KeyRepositoryNew
import com.showtime.wallet.databinding.FragmentConfirmSwapBinding
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.AppConstants
import com.showtime.wallet.utils.clickNoRepeat
import com.showtime.wallet.vm.SwapVModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import kotlin.math.pow

class SwapConfirmFragment : BaseSecondaryFragment<FragmentConfirmSwapBinding, SwapVModel>() {

    private lateinit var token1: Token
    private lateinit var token2: Token
    private lateinit var quoteResp: TokenPairUpdatedResp
    private lateinit var myAccount: Keypair

    companion object {
        fun getBundle(token1: Token, token2: Token, quoteResp: TokenPairUpdatedResp): Bundle {
            val bundle = Bundle()
            bundle.putParcelable("token1", token1)
            bundle.putParcelable("token2", token2)
            bundle.putParcelable("quoteResp", quoteResp)
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

        quoteResp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            extras.getParcelable("quoteResp", TokenPairUpdatedResp::class.java)!!
        else
            extras.getParcelable("quoteResp")!!

        GlobalScope.launch(Dispatchers.IO) {
            Ed25519KeyRepositoryNew.getByPublicKey(
                extras.getString(AppConstants.SELECTED_PUBLIC_KEY, "")
            )?.let {
                myAccount = it
            }
        }

        log("token==${token1}")
        log("token2==${token2}")
        log("quoteResp==${quoteResp}")
    }

    override fun getContentViewLayoutID() = R.layout.fragment_confirm_swap

    override fun initLiveDataObserve() {
        mViewModel.getSwapTransaction.observeForever {
            log("swapTx: ${it.swapTransaction}")
            val data = it.swapTransaction.toByteArray()

            val handler = Handler()
            object : Thread() {
                override fun run() {
                    val connection = Connection(RpcUrl.MAINNNET)
                    val blockhash = connection.getLatestBlockhash()

                    val transaction = Transaction.from(it.swapTransaction)

//                    val instruction =
//                        BaseInstruction(
//                            data, listOf(
//                                AccountMeta(myAccount.publicKey, writable = true, signer = true)
//                            ),
//                            SYSTEM_PROGRAM
//                        )
//                    val transaction =
//                        Transaction(blockhash, instruction, feePayer = myAccount.publicKey)

                    transaction.sign(myAccount)
                    try {
                        val signature = connection.sendTransaction(transaction)
                        log("signature: $signature")

//                TransactionStatusActivity.start(
//                    this@SwapConfirmActivity,
//                    TransactionStatusActivity.TYPE_SWAP,
//                    signature
//                )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }
    }

    private fun decodeLength(bytes: ByteArray): Int {
        var len = 0
        var size = 0
        var i = 0
        while (i < bytes.size) {
            val elem = bytes[i].toInt() and 0xFF
            len = len or (elem and 0x7F shl (size * 7))
            size++
            if (elem and 0x80 == 0) {
                break
            }
            i++
        }
        return len
    }

    val SIGNATURE_LENGTH_IN_BYTES = 64

//    fun deserialize(serializedTransaction: ByteArray): VersionedTransaction {
//        val byteArray = serializedTransaction.toTypedArray()
//
//        val signatures = mutableListOf<Byte>()
//        val signaturesLength = decodeLength(serializedTransaction)
//        for (i in 0 until signaturesLength) {
//            signatures += byteArray.sliceArray(0 until SIGNATURE_LENGTH_IN_BYTES)
//            byteArray.drop(SIGNATURE_LENGTH_IN_BYTES)
//        }
//
//        val message = VersionedMessage.deserialize(byteArray)
//        return VersionedTransaction(message, signatures.toTypedArray())
//    }

    private fun getVersionedTransaction() {
        /**
         * // deserialize the transaction
         * const swapTransactionBuf = Buffer.from(swapTransaction, 'base64');
         * var transaction = VersionedTransaction.deserialize(swapTransactionBuf);
         * console.log(transaction);
         *
         * // sign the transaction
         * transaction.sign([wallet.payer]);
         *
         * static deserialize(serializedTransaction: Uint8Array): VersionedTransaction {
         *     let byteArray = [...serializedTransaction];
         *
         *     const signatures = [];
         *     const signaturesLength = shortvec.decodeLength(byteArray);
         *     for (let i = 0; i < signaturesLength; i++) {
         *       signatures.push(
         *         new Uint8Array(guardedSplice(byteArray, 0, SIGNATURE_LENGTH_IN_BYTES)),
         *       );
         *     }
         *
         *     const message = VersionedMessage.deserialize(new Uint8Array(byteArray));
         *     return new VersionedTransaction(message, signatures);
         *   }
         *
         *   export type VersionedMessage = Message | MessageV0;
         * // eslint-disable-next-line no-redeclare
         * export const VersionedMessage = {
         *   deserializeMessageVersion(serializedMessage: Uint8Array): 'legacy' | number {
         *     const prefix = serializedMessage[0];
         *     const maskedPrefix = prefix & VERSION_PREFIX_MASK;
         *
         *     // if the highest bit of the prefix is not set, the message is not versioned
         *     if (maskedPrefix === prefix) {
         *       return 'legacy';
         *     }
         *
         *     // the lower 7 bits of the prefix indicate the message version
         *     return maskedPrefix;
         *   },
         *
         *   deserialize: (serializedMessage: Uint8Array): VersionedMessage => {
         *     const version =
         *       VersionedMessage.deserializeMessageVersion(serializedMessage);
         *     if (version === 'legacy') {
         *       return Message.from(serializedMessage);
         *     }
         *
         *     if (version === 0) {
         *       return MessageV0.deserialize(serializedMessage);
         *     } else {
         *       throw new Error(
         *         `Transaction message version ${version} deserialization is not supported`,
         *       );
         *     }
         *   },
         * };
         */
    }

    override fun initRequestData() {
    }

    @SuppressLint("SetTextI18n")
    override fun FragmentConfirmSwapBinding.initView() {
        token1.let {
            //Payment Section
            ImageHelper.obtainImage(requireContext(), it.logo, mBinding.paymentIcon)
            mBinding.paymentLabel.text = ""
            mBinding.paymentAmount.text = "${it.uiAmount} ${it.symbol}"
            mBinding.paymentValue.text = ""
        }
        token2.let {
            //Exchange Section
            ImageHelper.obtainImage(requireContext(), it.logo, mBinding.exchangeIcon)
            mBinding.exchangeLabel.text = ""
            mBinding.exchangeAmount.text = "${it.uiAmount} ${it.symbol}"
            mBinding.exchangeValue.text = ""
        }


        val price = (token2.uiAmount / 10.0.pow(token2.decimals)) /
                (token1.uiAmount / 10.0.pow(token1.decimals))

        lowestPriceValue.text = "1 ${token1.symbol} ≈ $price ${token2.symbol}"

        swapButton.clickNoRepeat {
            log("LFG!")
            mViewModel.doSwap(myAccount.publicKey.toBase58(), quoteResp)
        }
    }
}