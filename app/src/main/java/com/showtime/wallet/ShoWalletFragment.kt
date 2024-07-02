package com.showtime.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.showtime.wallet.databinding.FragmentFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.sol4k.Base58
import org.sol4k.Connection
import org.sol4k.Keypair
import org.sol4k.PublicKey
import org.sol4k.RpcUrl
import org.sol4k.Transaction
import org.sol4k.instruction.TransferInstruction

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ShoWalletFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var sender: Keypair

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onAccountConnected(keypair: Keypair) {
        sender = keypair
        showAccount()
        loadBalance()
    }

    private fun showAccount(){
        binding.key.text = "account: " + sender.publicKey.toBase58()
    }

    private fun loadBalance(){
        binding.bal.text = "loading balance...."

        GlobalScope.launch(Dispatchers.IO) {
            val getBalance = async {
                val connection = Connection(RpcUrl.DEVNET)
                connection.getBalance(sender.publicKey).toString()
            }
            val balance = getBalance.await()

            binding.bal.text = "balance: " + balance
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getPrivateKey.setOnClickListener {
            val privateKey = byteArrayToBase58(sender.secret)
            copyToClipboard(privateKey)
        }

        binding.fromPrivateKey.setOnClickListener {
            getTextFromClipboard()?.let {
                onAccountConnected(Keypair.fromSecretKey(base58ToByteArray(it)))
            }
        }

        binding.generate.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val app = requireActivity().application as ShowVaultApplication
                val keypair = async(Dispatchers.IO) {
                    app.keyRepository.generateKeypair()
                }

                val privateKey = keypair.await().private as Ed25519PrivateKeyParameters
                onAccountConnected(Keypair.fromSecretKey(privateKey.encoded))
            }
        }

        binding.send.setOnClickListener {
            binding.sig.text = "sending SOL to 9KUpYG22qNKSW3XkpZmBCyFiy1JsFC7rw6o4c7iUomEk..."
            val handler = Handler()
            object : Thread() {
                override fun run() {
                    val connection = Connection(RpcUrl.DEVNET)
                    val blockhash = connection.getLatestBlockhash()
                    val receiver = PublicKey("9KUpYG22qNKSW3XkpZmBCyFiy1JsFC7rw6o4c7iUomEk")
                    val instruction =
                        TransferInstruction(sender.publicKey, receiver, lamports = 1000)
                    val transaction =
                        Transaction(blockhash, instruction, feePayer = sender.publicKey)
                    transaction.sign(sender)

                    try {
                        val signature = connection.sendTransaction(transaction)
                        handler.post {
                            binding.sig.text = signature
                        }
                    } catch (e: Exception) {
                        binding.sig.text = e.message
                    }
                }
            }.start()
        }

        GlobalScope.launch(Dispatchers.Main) {
            val app = requireActivity().application as ShowVaultApplication
            val keypair = async(Dispatchers.IO) {
                app.keyRepository.getOne()
            }

            val privateKey = keypair.await()?.private as Ed25519PrivateKeyParameters?
            if (privateKey != null) {
                onAccountConnected(Keypair.fromSecretKey(privateKey.encoded))
            }
        }
    }

    private fun getTextFromClipboard(): String? {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Check if there is primary clip data available
        return if (clipboard.hasPrimaryClip()) {
            val clipData = clipboard.primaryClip
            val item = clipData?.getItemAt(0)
            item?.text?.toString()
        } else {
            null
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "copied: " + text, Toast.LENGTH_SHORT).show()
    }

    fun base58ToByteArray(base58String: String): ByteArray {
        return Base58.decode(base58String)
    }

    fun byteArrayToBase58(byteArray: ByteArray): String {
        return Base58.encode(byteArray)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}