package org.sol4k.transaction

import kotlin.experimental.and

class VersionedMessage {

    companion object{

        val VERSION_PREFIX_MASK: Byte = 0x7f

        fun deserializeMessageVersion(serializedMessage: ByteArray): String {
            val prefix = serializedMessage[0]
            val maskedPrefix = prefix and VERSION_PREFIX_MASK

            // if the highest bit of the prefix is not set, the message is not versioned
            if (maskedPrefix == prefix) {
                return "legacy"
            }

            return maskedPrefix.toString()
        }

        fun deserialize(serializedMessage: ByteArray): IMessage? {
            val version = deserializeMessageVersion(serializedMessage)
            if (version == "legacy") {
                return Message.from(serializedMessage)
            }
            if (version == "0") {
                return MessageV0.deserialize(serializedMessage);
            }

            return null
        }
    }

}