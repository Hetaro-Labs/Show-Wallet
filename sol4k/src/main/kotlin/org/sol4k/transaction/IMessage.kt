package org.sol4k.transaction

interface IMessage {
    fun serialize(): ByteArray
}