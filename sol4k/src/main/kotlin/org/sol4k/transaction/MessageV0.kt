package org.sol4k.transaction

import org.sol4k.Base58
import org.sol4k.PublicKey
import kotlin.experimental.and

class MessageV0(val args: Args) : IMessage {

    var header: Header = args.header
    var staticAccountKeys: List<PublicKey> = args.staticAccountKeys
    var recentBlockhash: String = args.recentBlockhash
    var compiledInstructions: List<CompiledInstruction> = args.compiledInstructions
    var addressTableLookups: List<AddressTableLookup> = args.addressTableLookups

    val version: Int
        get() = 0

    val numAccountKeysFromLookups: Int
        get() {
            var count = 0
            for (lookup in addressTableLookups) {
                count += lookup.readonlyIndexes.size + lookup.writableIndexes.size
            }
            return count
        }

    fun getAccountKeys(args: GetAccountKeysArgs?): MessageAccountKeys {
        var accountKeysFromLookups: AccountKeysFromLookups? = null
        if (args?.accountKeysFromLookups != null) {
            if (numAccountKeysFromLookups != args.accountKeysFromLookups.writable.size + args.accountKeysFromLookups.readonly.size) {
                throw Error("Failed to get account keys because of a mismatch in the number of account keys from lookups")
            }
            accountKeysFromLookups = args.accountKeysFromLookups
        } else if (args?.addressLookupTableAccounts != null) {
            accountKeysFromLookups = resolveAddressTableLookups(args.addressLookupTableAccounts)
        } else if (addressTableLookups.isNotEmpty()) {
            throw Error("Failed to get account keys because address table lookups were not resolved")
        }
        return MessageAccountKeys(staticAccountKeys, accountKeysFromLookups)
    }

    fun isAccountSigner(index: Int): Boolean {
        return index < header.numRequiredSignatures
    }

    fun isAccountWritable(index: Int): Boolean {
        val numSignedAccounts = header.numRequiredSignatures
        val numStaticAccountKeys = staticAccountKeys.size
        return if (index >= numStaticAccountKeys) {
            val lookupAccountKeysIndex = index - numStaticAccountKeys
            val numWritableLookupAccountKeys = addressTableLookups.sumOf { it.writableIndexes.size }
            lookupAccountKeysIndex < numWritableLookupAccountKeys
        } else if (index >= header.numRequiredSignatures) {
            val unsignedAccountIndex = index - numSignedAccounts
            val numUnsignedAccounts = numStaticAccountKeys - numSignedAccounts
            val numWritableUnsignedAccounts =
                numUnsignedAccounts - header.numReadonlyUnsignedAccounts
            unsignedAccountIndex < numWritableUnsignedAccounts
        } else {
            val numWritableSignedAccounts = numSignedAccounts - header.numReadonlySignedAccounts
            index < numWritableSignedAccounts
        }
    }

    fun resolveAddressTableLookups(addressLookupTableAccounts: List<AddressLookupTableAccount>): AccountKeysFromLookups {
        val accountKeysFromLookups = AccountKeysFromLookups(mutableListOf(), mutableListOf())
        for (tableLookup in addressTableLookups) {
            val tableAccount = addressLookupTableAccounts.find { it.key == tableLookup.accountKey }
                ?: throw Error("Failed to find address lookup table account for table key ${tableLookup.accountKey}")
            for (index in tableLookup.writableIndexes) {
                if (index < tableAccount.state.addresses.size) {
                    accountKeysFromLookups.writable.add(tableAccount.state.addresses[index.toInt()])
                } else {
                    throw Error("Failed to find address for index $index in address lookup table ${tableLookup.accountKey}")
                }
            }
            for (index in tableLookup.readonlyIndexes) {
                if (index < tableAccount.state.addresses.size) {
                    accountKeysFromLookups.readonly.add(tableAccount.state.addresses[index.toInt()])
                } else {
                    throw Error("Failed to find address for index $index in address lookup table ${tableLookup.accountKey}")
                }
            }
        }
        return accountKeysFromLookups
    }

    override fun serialize(): ByteArray {
        return args.buffer
    }

    private fun serializeInstructions(): ByteArray {
        // TODO Implement instruction serialization logic
        return byteArrayOf()
    }

    private fun serializeAddressTableLookups(): ByteArray {
        // TODO Implement address table lookup serialization logic
        return byteArrayOf()
    }

    companion object {

        val VERSION_PREFIX_MASK: Byte = 0x7f
        val PUBLIC_KEY_LENGTH = 32

        // TODO Implement compilation logic
//        fun compile(args: CompileArgs): MessageV0 {
//            return MessageV0(Args())
//        }

        fun deserialize(buffer: ByteArray): MessageV0 {
            val byteArray = buffer.toMutableList()
            val prefix = TransactionHelper.guardedShift(byteArray)

            val maskedPrefix = prefix.and(VERSION_PREFIX_MASK)
            require(prefix != maskedPrefix) { "Expected versioned message but received legacy message" }
            require(maskedPrefix.toInt() == 0) { "Expected versioned message with version 0 but found version $prefix" }

            val numRequiredSignatures = TransactionHelper.guardedShift(byteArray).toInt()

            val numReadonlySignedAccounts = TransactionHelper.guardedShift(byteArray).toInt()

            val numReadonlyUnsignedAccounts = TransactionHelper.guardedShift(byteArray).toInt()

            val header = Header(
                numRequiredSignatures,
                numReadonlySignedAccounts,
                numReadonlyUnsignedAccounts
            )

            val staticAccountKeysLength = TransactionHelper.decodeLength(byteArray)
            val staticAccountKeys = mutableListOf<PublicKey>()
            repeat(staticAccountKeysLength) {
                staticAccountKeys.add(
                    PublicKey(
                        TransactionHelper.guardedSplice(
                            byteArray,
                            0,
                            PUBLIC_KEY_LENGTH
                        ).toByteArray()
                    )
                )
            }

            val recentBlockhash = Base58.encode(
                TransactionHelper.guardedSplice(byteArray, 0, PUBLIC_KEY_LENGTH).toByteArray()
            )

            val instructionCount = TransactionHelper.decodeLength(byteArray)
            val compiledInstructions = mutableListOf<CompiledInstruction>()
            repeat(instructionCount) {
                val programIdIndex = TransactionHelper.guardedShift(byteArray)
                val accountKeyIndexesLength = TransactionHelper.decodeLength(byteArray)
                val accountKeyIndexes =
                    TransactionHelper.guardedSplice(byteArray, 0, accountKeyIndexesLength)
                val dataLength = TransactionHelper.decodeLength(byteArray)
                val data =
                    TransactionHelper.guardedSplice(byteArray, 0, dataLength) // Assuming ByteArray

                compiledInstructions.add(
                    CompiledInstruction(
                        programIdIndex.toInt(),
                        accountKeyIndexes,
                        data.toByteArray()
                    )
                )
            }

            val addressTableLookupsCount = TransactionHelper.decodeLength(byteArray)
            val addressTableLookups = mutableListOf<AddressTableLookup>()
            repeat(addressTableLookupsCount) {
                val accountKey = PublicKey(
                    TransactionHelper.guardedSplice(byteArray, 0, PUBLIC_KEY_LENGTH).toByteArray()
                )
                val writableIndexesLength = TransactionHelper.decodeLength(byteArray)
                val writableIndexes =
                    TransactionHelper.guardedSplice(byteArray, 0, writableIndexesLength)
                val readonlyIndexesLength = TransactionHelper.decodeLength(byteArray)
                val readonlyIndexes =
                    TransactionHelper.guardedSplice(byteArray, 0, readonlyIndexesLength)

                addressTableLookups.add(
                    AddressTableLookup(
                        accountKey,
                        writableIndexes,
                        readonlyIndexes
                    )
                )
            }

            return MessageV0(
                Args(
                    buffer,
                    header,
                    staticAccountKeys,
                    recentBlockhash,
                    compiledInstructions,
                    addressTableLookups
                )
            )
        }
    }
}

// Define necessary data classes and helper functions
data class Args(
    val buffer: ByteArray,
    val header: Header = Header(),
    val staticAccountKeys: List<PublicKey> = listOf(),
    val recentBlockhash: String = "",
    val compiledInstructions: List<CompiledInstruction> = listOf(),
    val addressTableLookups: List<AddressTableLookup> = listOf()
)

data class Header(
    val numRequiredSignatures: Int = 0,
    val numReadonlySignedAccounts: Int = 0,
    val numReadonlyUnsignedAccounts: Int = 0
)

data class CompiledInstruction(
    val programIdIndex: Int,
    val accountKeyIndexes: List<Byte>,
    val data: ByteArray
)

data class AddressTableLookup(
    val accountKey: PublicKey,
    val writableIndexes: List<Byte>,
    val readonlyIndexes: List<Byte>
)

data class AddressLookupTableAccount(
    val key: PublicKey,
    val state: AddressState
)

data class AddressState(
    val addresses: List<PublicKey>
)


data class AccountKeysFromLookups(
    val writable: MutableList<PublicKey>,
    val readonly: MutableList<PublicKey>
)

data class GetAccountKeysArgs(
    val accountKeysFromLookups: AccountKeysFromLookups? = null,
    val addressLookupTableAccounts: List<AddressLookupTableAccount>? = null
)

data class CompileArgs(
    val instructions: List<CompiledInstruction>,
    val payerKey: PublicKey,
    val addressLookupTableAccounts: List<AddressLookupTableAccount>? = null,
    val recentBlockhash: String
)

class MessageAccountKeys(
    val staticAccountKeys: List<PublicKey>,
    val accountKeysFromLookups: AccountKeysFromLookups?
) {
    fun compileInstructions(instructions: List<CompiledInstruction>): List<CompiledInstruction> {
        // Implement instruction compilation logic
        return listOf()
    }
}