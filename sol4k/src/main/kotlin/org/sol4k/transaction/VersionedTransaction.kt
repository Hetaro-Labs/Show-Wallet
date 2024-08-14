package org.sol4k.transaction

import org.sol4k.Base58
import org.sol4k.Binary
import org.sol4k.Keypair
import java.nio.ByteBuffer
import java.util.Base64


class VersionedTransaction(val message: IMessage?, val signatures: MutableList<ByteArray>) {

    private val SIGNATURE_LENGTH = 64

    companion object {

        val SIGNATURE_LENGTH_IN_BYTES = 64

        fun deserialize(encodedTransaction: String): VersionedTransaction {
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

            val message = VersionedMessage.deserialize(byteArray.toByteArray())
            return VersionedTransaction(message, signatures)
        }
    }

    /** helpme: turn this code into kotlin
    serialize() {
    const serializedMessage = this.message.serialize();
    const encodedSignaturesLength = Array();
    encodeLength(encodedSignaturesLength, this.signatures.length);
    const transactionLayout = BufferLayout__namespace.struct([BufferLayout__namespace.blob(encodedSignaturesLength.length, 'encodedSignaturesLength'), BufferLayout__namespace.seq(signature(), this.signatures.length, 'signatures'), BufferLayout__namespace.blob(serializedMessage.length, 'serializedMessage')]);
    const serializedTransaction = new Uint8Array(2048);
    const serializedTransactionLength = transactionLayout.encode({
    encodedSignaturesLength: new Uint8Array(encodedSignaturesLength),
    signatures: this.signatures,
    serializedMessage
    }, serializedTransaction);
    return serializedTransaction.slice(0, serializedTransactionLength);
    }
     */

    fun serialize(): ByteArray {
        val serializedMessage = message?.serialize() ?: ByteArray(0)
        val encodedSignaturesLength = mutableListOf<Byte>()
        TransactionHelper.encodeLength(encodedSignaturesLength, signatures.size)

        val totalLength = encodedSignaturesLength.size + (SIGNATURE_LENGTH * signatures.size) + serializedMessage.size
        val serializedTransaction = ByteArray(totalLength)

        var offset = 0

        // Copy encodedSignaturesLength
        encodedSignaturesLength.forEach {
            serializedTransaction[offset++] = it
        }

        // Copy signatures
        signatures.forEach { signature ->
            System.arraycopy(signature, 0, serializedTransaction, offset, SIGNATURE_LENGTH)
            offset += SIGNATURE_LENGTH
        }

        // Copy serializedMessage
        System.arraycopy(serializedMessage, 0, serializedTransaction, offset, serializedMessage.size)

        return serializedTransaction
    }
}