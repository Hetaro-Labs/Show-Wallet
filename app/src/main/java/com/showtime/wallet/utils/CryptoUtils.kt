package com.showtime.wallet.utils

import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.crypto.MnemonicCode
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import org.sol4k.Base58
import org.sol4k.Keypair
import java.security.SecureRandom
import java.security.Security

class CryptoUtils{

    companion object{

        fun getDisplayAddress(address: String?): String{
            if (address == null) return ""
            else if(!isValidSolanaAddress(address)) return address
            else return address.substring(0, 5) + "..." + address.substring(address.length-5, address.length)
        }

        fun isValidSolanaAddress(address: String): Boolean {
            // Check if the address length is correct
            if (address.length != 44) {
                return false
            }

            return try {
                // Decode the address using Base58
                val decodedBytes = Base58.decode(address)

                // Check if the decoded byte array length is 32 (256 bits)
                decodedBytes.size == 32
            } catch (e: Exception) {
                // If decoding fails, the address is not valid
                false
            }
        }

        fun convertKeypair(keypair: AsymmetricCipherKeyPair): Keypair{
            val privateKey = keypair.private as Ed25519PrivateKeyParameters
            return Keypair.fromSecretKey(privateKey.encoded)
        }

        fun keypairToPublicKey(keypair: AsymmetricCipherKeyPair): String{
            return convertKeypair(keypair).publicKey.toBase58()
        }

        private fun mnemonicToSeed(mnemonic: List<String>, passphrase: String = ""): ByteArray {
            return MnemonicCode.toSeed(mnemonic, passphrase)
        }

        private fun derivePrivateKey(seed: ByteArray): ByteArray {
            val rootKey = HDKeyDerivation.createMasterPrivateKey(seed)
            val childKey = HDKeyDerivation.deriveChildKey(rootKey, ChildNumber(0, true))

            return childKey.privKeyBytes
        }

        fun mnemonicToPrivateKeyBytes(mnemonic: List<String>, passphrase: String = ""): ByteArray {
            return derivePrivateKey(mnemonicToSeed(mnemonic, passphrase))
        }

        fun mnemonicToKeypair(mnemonic: List<String>, passphrase: String = ""): Keypair{
            val bytes = derivePrivateKey(mnemonicToSeed(mnemonic, passphrase))
            return Keypair.fromSecretKey(bytes)
        }

        private fun generateEd25519KeyPair(privateKey: ByteArray) {
            val ed25519PrivateKeyParams = Ed25519PrivateKeyParameters(privateKey, 0)
            val privateKeyEncoded = ed25519PrivateKeyParams.encoded

            println("Private Key: ${Hex.toHexString(privateKeyEncoded)}")

            val publicKeyParams = ed25519PrivateKeyParams.generatePublicKey()
            val publicKeyEncoded = publicKeyParams.encoded

            println("Public Key: ${Hex.toHexString(publicKeyEncoded)}")
        }

        fun mnemonicToPrivateKey(mnemonic: List<String>): String  {
            val privateKey = derivePrivateKey(mnemonicToSeed(mnemonic))
            val ed25519PrivateKeyParams = Ed25519PrivateKeyParameters(privateKey, 0)
            val privateKeyEncoded = ed25519PrivateKeyParams.encoded
            return Hex.toHexString(privateKeyEncoded)
        }

        fun mnemonicToPublicKey(mnemonic: List<String>): String  {
            val privateKey = derivePrivateKey(mnemonicToSeed(mnemonic))
            val ed25519PrivateKeyParams = Ed25519PrivateKeyParameters(privateKey, 0)

            val publicKeyParams = ed25519PrivateKeyParams.generatePublicKey()
            val publicKeyEncoded = publicKeyParams.encoded
            return Hex.toHexString(publicKeyEncoded)
        }

        fun generateMnemonic(): List<String> {
            Security.addProvider(BouncyCastleProvider())
            val entropy = generateEntropy()
            return entropyToMnemonic(entropy)
        }

        private fun generateEntropy(): ByteArray {
            val secureRandom = SecureRandom()
            val entropy = ByteArray(16) // 128 bits of entropy
            secureRandom.nextBytes(entropy)
            return entropy
        }

        private fun entropyToMnemonic(entropy: ByteArray): List<String> {
            val mnemonicCode = MnemonicCode()
            return mnemonicCode.toMnemonic(entropy)
        }
    }

}