package org.sol4k.transaction

import org.sol4k.Base58
import org.sol4k.PublicKey


/**
     * /**
     *    * Decode a compiled message into a Message object.
     *    */
     *   static from(buffer$1) {
     *     // Slice up wire data
     *     let byteArray = [...buffer$1];
     *     const numRequiredSignatures = guardedShift(byteArray);
     *     if (numRequiredSignatures !== (numRequiredSignatures & VERSION_PREFIX_MASK)) {
     *       throw new Error('Versioned messages must be deserialized with VersionedMessage.deserialize()');
     *     }
     *     const numReadonlySignedAccounts = guardedShift(byteArray);
     *     const numReadonlyUnsignedAccounts = guardedShift(byteArray);
     *     const accountCount = decodeLength(byteArray);
     *     let accountKeys = [];
     *     for (let i = 0; i < accountCount; i++) {
     *       const account = guardedSplice(byteArray, 0, PUBLIC_KEY_LENGTH);
     *       accountKeys.push(new PublicKey(buffer.Buffer.from(account)));
     *     }
     *     const recentBlockhash = guardedSplice(byteArray, 0, PUBLIC_KEY_LENGTH);
     *     const instructionCount = decodeLength(byteArray);
     *     let instructions = [];
     *     for (let i = 0; i < instructionCount; i++) {
     *       const programIdIndex = guardedShift(byteArray);
     *       const accountCount = decodeLength(byteArray);
     *       const accounts = guardedSplice(byteArray, 0, accountCount);
     *       const dataLength = decodeLength(byteArray);
     *       const dataSlice = guardedSplice(byteArray, 0, dataLength);
     *       const data = bs58__default.default.encode(buffer.Buffer.from(dataSlice));
     *       instructions.push({
     *         programIdIndex,
     *         accounts,
     *         data
     *       });
     *     }
     *     const messageArgs = {
     *       header: {
     *         numRequiredSignatures,
     *         numReadonlySignedAccounts,
     *         numReadonlyUnsignedAccounts
     *       },
     *       recentBlockhash: bs58__default.default.encode(buffer.Buffer.from(recentBlockhash)),
     *       accountKeys,
     *       instructions
     *     };
     *     return new Message(messageArgs);
     *   }
     */


class Message(val buffer: ByteArray, val header: Header, val recentBlockhash: String, val accountKeys: List<PublicKey>, val instructions: List<Instruction>): IMessage {

    data class Header(val numRequiredSignatures: Int, val numReadonlySignedAccounts: Int, val numReadonlyUnsignedAccounts: Int)
    data class Instruction(val programIdIndex: Int, val accounts: List<Byte>, val data: String)

    companion object {

        // Define constants
        val VERSION_PREFIX_MASK = 0x7F
        val PUBLIC_KEY_LENGTH = 32

        fun from(buffer: ByteArray): Message {
            val byteArray = buffer.toMutableList()
            val numRequiredSignatures = guardedShift(byteArray)
            if (numRequiredSignatures.toInt() != (numRequiredSignatures.toInt().and(
                    VERSION_PREFIX_MASK
                ))) {
                throw Exception("Versioned messages must be deserialized with VersionedMessage.deserialize()")
            }

            val numReadonlySignedAccounts = guardedShift(byteArray)
            val numReadonlyUnsignedAccounts = guardedShift(byteArray)
            val accountCount = decodeLength(byteArray)

            val accountKeys = mutableListOf<PublicKey>()
            for (i in 0 until accountCount) {
                val account = guardedSplice(byteArray, 0, PUBLIC_KEY_LENGTH)
                accountKeys.add(PublicKey(account.toByteArray()))
            }

            val recentBlockhash = guardedSplice(byteArray, 0, PUBLIC_KEY_LENGTH)
            val instructionCount = decodeLength(byteArray)

            val instructions = mutableListOf<Instruction>()
            for (i in 0 until instructionCount) {
                val programIdIndex = guardedShift(byteArray)
                val accountCount = decodeLength(byteArray)
                val accounts = guardedSplice(byteArray, 0, accountCount)
                val dataLength = decodeLength(byteArray)
                val dataSlice = guardedSplice(byteArray, 0, dataLength)
                val data = Base58.encode(dataSlice.toByteArray())

                instructions.add(Instruction(programIdIndex.toInt(), accounts, data))
            }

            val messageArgs = Header(
                numRequiredSignatures.toInt(),
                numReadonlySignedAccounts.toInt(),
                numReadonlyUnsignedAccounts.toInt()
            )

            return Message(
                buffer,
                messageArgs,
                Base58.encode(recentBlockhash.toByteArray()),
                accountKeys,
                instructions
            )
        }

        fun guardedShift(byteArray: MutableList<Byte>): Byte {
            if (byteArray.isEmpty()) {
                throw Exception("END_OF_BUFFER_ERROR_MESSAGE")
            }
            return byteArray.removeAt(0)
        }

        fun decodeLength(bytes: MutableList<Byte>): Int {
            var len = 0
            var size = 0
            while (true) {
                val elem = bytes.removeAt(0)
                len = len or ((elem.toInt() and 0x7F) shl (size * 7))
                size += 1
                if ((elem.toInt() and 0x80) == 0) {
                    break
                }
            }
            return len
        }


        fun guardedSplice(byteArray: MutableList<Byte>, start: Int, deleteCount: Int? = null, vararg items: Byte): List<Byte> {
            val deleteCountValue = deleteCount ?: 0

            if ((deleteCount != null && start + deleteCountValue > byteArray.size) || start >= byteArray.size) {
                throw Exception("END_OF_BUFFER_ERROR_MESSAGE")
            }

            val removed = mutableListOf<Byte>()
            for (i in 0 until deleteCountValue) {
                if (start < byteArray.size) {
                    removed.add(byteArray.removeAt(start))
                }
            }

            byteArray.addAll(start, items.toList())
            return removed
        }

    }

    override fun serialize(): ByteArray {
        return buffer
    }

}