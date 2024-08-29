package com.showtime.wallet.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.showtime.wallet.net.QuickNodeUrl
import com.showtime.wallet.net.bean.Token
import com.showtime.wallet.utils.CryptoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sol4k.Connection
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.exception.RpcException
import org.sol4k.instruction.CreateAssociatedTokenAccountInstruction
import org.sol4k.instruction.Instruction
import org.sol4k.instruction.SplTransferInstruction
import org.sol4k.instruction.TransferInstruction

class SendTokenVModel : BaseWalletVModel() {

    private val TAG = SendTokenVModel::class.simpleName

    private val _getTokenAccountBalance = MutableLiveData<UpdateTokenAccountBalance>()
    val getTokenAccountBalance: MutableLiveData<UpdateTokenAccountBalance> = _getTokenAccountBalance

    private val _getEnterAmountErr = MutableLiveData<Boolean>()
    val getEnterAmountErr: MutableLiveData<Boolean> = _getEnterAmountErr

    private val _getAddressErr = MutableLiveData<Boolean>()
    val getAddressErr: MutableLiveData<Boolean> = _getAddressErr

    fun onAddressChange(it: String) {
        if (CryptoUtils.isValidSolanaAddress(it)) {
            _getAddressErr.postValue(false)
        } else {
            _getAddressErr.postValue(true)
        }
    }

    fun onAmountChange(token: Token, it: String) {
        val amount = if (it.isEmpty()) 0.0 else it.toDouble()

        if (amount > token.uiAmount || amount <= 0.0) {
            _getEnterAmountErr.postValue(true)
        } else {
            _getEnterAmountErr.postValue(false)
        }
    }

    fun getTokenAccountBalance(token: Token, type: String = "") {
        viewModelScope.launch {
            try {
                val connection = Connection(RpcUrl.DEVNET)
                val bean = withContext(Dispatchers.IO) {
                    connection.getTokenAccountBalance(PublicKey(token.tokenAccount))
                }

                Log.d(TAG, "getTokenAccountBalance==${bean}")
                _getTokenAccountBalance.postValue(
                    UpdateTokenAccountBalance(
                        bean.uiAmount,
                        type
                    )
                )
            } catch (e: RuntimeException) {
            } catch (e: RpcException) {
            }
        }
    }


    fun sendSolToken(receiver: PublicKey, amount: Long) {
        log(
            "send SOL: ${myAccount.publicKey.toBase58()}, \n" +
                    "${receiver.toBase58()}, \n"
        )

        val connection = Connection(QuickNodeUrl.MAINNNET)

        viewModelScope.launch {
            val blockhash = withContext(Dispatchers.IO) {
                connection.getLatestBlockhash()
            }

            val instruction =
                TransferInstruction(myAccount.publicKey, receiver, lamports = amount)
            val transaction =
                Transaction(blockhash, instruction, feePayer = myAccount.publicKey)
            transaction.sign(myAccount)

            try {
                val signature = withContext(Dispatchers.IO) {
                    connection.sendTransaction(transaction)
                }

                _getTransactionHash.postValue(signature)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun sendSPLToken(
        tokenMintAddress: PublicKey,
        receiverAddress: PublicKey,
        amount: Long
    ) {
        val connection = Connection(QuickNodeUrl.MAINNNET)

        val instructions = ArrayList<Instruction>()

        val fromAssociatedTokenAddress =
            PublicKey.findProgramDerivedAddress(myAccount.publicKey, tokenMintAddress)
        val toAssociatedTokenAddress =
            PublicKey.findProgramDerivedAddress(receiverAddress, tokenMintAddress)

        val destination = connection.getAccountInfo(toAssociatedTokenAddress.publicKey)
        if (destination == null) {
            instructions.add(
                CreateAssociatedTokenAccountInstruction(
                    myAccount.publicKey,
                    toAssociatedTokenAddress.publicKey,
                    receiverAddress,
                    tokenMintAddress
                )
            )
        }

        log(
            "send SPL: ${myAccount.publicKey.toBase58()}, \n" +
                    "${fromAssociatedTokenAddress.publicKey.toBase58()}, \n" +
                    "${toAssociatedTokenAddress.publicKey.toBase58()}, \n" +
                    "${receiverAddress.toBase58()}, \n" +
                    "${tokenMintAddress.toBase58()}, \n"
        )

        instructions.add(
            SplTransferInstruction(
                fromAssociatedTokenAddress.publicKey,
                toAssociatedTokenAddress.publicKey,
                myAccount.publicKey,
                amount
            )
        )

        viewModelScope.launch {
            val blockhash = withContext(Dispatchers.IO) {
                connection.getLatestBlockhash()
            }

            val transaction = Transaction(blockhash, instructions, feePayer = myAccount.publicKey)
            transaction.sign(myAccount)

            val signature = withContext(Dispatchers.IO) {
                connection.sendTransaction(transaction)
            }

            _getTransactionHash.postValue(signature)
        }
    }


    data class UpdateTokenAccountBalance(
        val amount: String,
        val type: String
    )

}