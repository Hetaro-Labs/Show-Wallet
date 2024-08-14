package org.sol4k.transaction

object TransactionHelper {

    fun encodeLength(bytes: MutableList<Byte>, len: Int) {
        var remLen = len
        while (true) {
            var elem = (remLen and 0x7f).toByte()
            remLen = remLen shr 7
            if (remLen == 0) {
                bytes.add(elem)
                break
            } else {
                elem = (elem.toInt() or 0x80).toByte()
                bytes.add(elem)
            }
        }
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

    fun guardedSplice(
        list: MutableList<Byte>,
        start: Int,
        deleteCount: Int,
        vararg elements: Byte
    ): List<Byte> {
        val removed = mutableListOf<Byte>()
        for (i in start until start + deleteCount) {
            removed.add(list[i])
        }
//        val removed = list.subList(start, start + deleteCount)

        for (i in start + deleteCount - 1 downTo start) {
            list.removeAt(i)
        }
        list.addAll(start, elements.asList())
        return removed
    }

    fun guardedShift(list: MutableList<Byte>): Byte {
        return list.removeAt(0)
    }

}