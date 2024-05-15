package com.example.myapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.databinding.FragmentFirstBinding
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
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sender: Keypair

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onAccountConnected(keypair: Keypair){
        sender = keypair
        binding.key.text = "Connected to: " + sender.publicKey.toBase58()
        loadBalance()
    }

    private fun loadBalance(){
        binding.bal.text = "loading balance...."
        val handler = Handler()
        object: Thread(){
            override fun run() {
                super.run()
                val connection = Connection(RpcUrl.DEVNET)
                val balance = connection.getBalance(sender.publicKey).toString()

                handler.post {
                    binding.bal.text = balance
                }
            }
        }.start()
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
            onAccountConnected(Keypair.generate())
        }

        binding.send.setOnClickListener {
            binding.sig.text = "sending SOL to 9KUpYG22qNKSW3XkpZmBCyFiy1JsFC7rw6o4c7iUomEk..."
            val handler = Handler()
            object: Thread() {
                override fun run() {
                    val connection = Connection(RpcUrl.DEVNET)
                    val blockhash = connection.getLatestBlockhash()
                    val receiver = PublicKey("9KUpYG22qNKSW3XkpZmBCyFiy1JsFC7rw6o4c7iUomEk")
                    val instruction = TransferInstruction(sender.publicKey, receiver, lamports = 1000)
                    val transaction = Transaction(blockhash, instruction, feePayer = sender.publicKey)
                    transaction.sign(sender)
                    val signature = connection.sendTransaction(transaction)

                    handler.post {
                        binding.sig.text = signature
                    }
                }
            }.start()

        }
    }

    private fun getTextFromClipboard(): String? {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

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
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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