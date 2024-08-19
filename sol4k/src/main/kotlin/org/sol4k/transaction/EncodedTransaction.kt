package org.sol4k.transaction

import org.sol4k.Binary
import org.sol4k.Keypair
import java.nio.ByteBuffer
import java.util.Base64


class EncodedTransaction(val message: ByteArray, val signatures: MutableList<ByteArray>) {

    private val SIGNATURE_LENGTH = 64

    companion object {

        val SIGNATURE_LENGTH_IN_BYTES = 64

        fun deserialize(encodedTransaction: String): EncodedTransaction {
            val byteArray = Base64.getDecoder().decode(encodedTransaction).toMutableList()
            val signatures = mutableListOf<ByteArray>()
            val signaturesDecodedLength = TransactionHelper.decodeLength(byteArray)
            for (i in 0 until signaturesDecodedLength) {
                signatures.add(
                    TransactionHelper.guardedSplice(
                        byteArray,
                        0,
                        SIGNATURE_LENGTH_IN_BYTES
                    ).toByteArray()
                )
            }

            //remove version
//            TransactionHelper.guardedShift(byteArray)
            return EncodedTransaction(byteArray.toByteArray(), signatures)
        }
    }

//    sign(signers) {
//        const messageData = this.message.serialize();
//        const signerPubkeys = this.message.staticAccountKeys.slice(0, this.message.header.numRequiredSignatures);
//        for (const signer of signers) {
//            const signerIndex = signerPubkeys.findIndex(pubkey => pubkey.equals(signer.publicKey));
//            assert(signerIndex >= 0, `Cannot sign with non signer key ${signer.publicKey.toBase58()}`);
//            this.signatures[signerIndex] = sign(messageData, signer.secretKey);
//        }
//    }

    fun sign(keypair: Keypair) {
        val signature = keypair.sign(message)
        signatures[0] = signature
//        signatures.add(signature)
    }


    fun serialize(): ByteArray {
        val signaturesLength = Binary.encodeLength(signatures.size)
        val buffer =
            ByteBuffer.allocate(
                signaturesLength.size + signatures.size * SIGNATURE_LENGTH + message.size,
            )
        buffer.put(signaturesLength)
        signatures.forEach { signature ->
            buffer.put(signature)
        }
        buffer.put(message)
        return buffer.array()
    }


}