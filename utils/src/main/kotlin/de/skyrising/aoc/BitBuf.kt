package de.skyrising.aoc

class BitBuf(private val data: ByteArray) {
    constructor(capacity: Int) : this(ByteArray(capacity)) {
        writerIndex = 0
    }

    var readerIndex = 0
        set(value) {
            if (value < 0) throw IndexOutOfBoundsException()
            if (value > writerIndex) throw IllegalArgumentException("readerIndex > writerIndex")
            field = value
        }
    var writerIndex = data.size * 8
        set(value) {
            if (value < 0 || value > data.size * 8) throw IndexOutOfBoundsException()
            field = value
        }

    fun readAvailable() = writerIndex - readerIndex
    fun writeAvailable() = data.size * 8 - writerIndex

    fun write(buf: ByteArray, offset: Int, length: Int) {
        if (offset < 0 || length < 0 || offset + length >= data.size) throw IndexOutOfBoundsException()
        if (writeAvailable() < length * 8) throw IndexOutOfBoundsException()
        for (i in 0 until length) {
            writeByte(data[offset + i].toInt())
        }
    }

    fun writeByte(b: Int) {
        if (writeAvailable() < 8) throw IndexOutOfBoundsException()
        if (bitIndex(writerIndex) == 0) {
            data[byteIndex(writerIndex)] = b.toByte()
        } else {
            val asInt = b and 0xff
            val byteIndex = byteIndex(writerIndex)
            val bitIndex = bitIndex(writerIndex)
            val firstMask = 0xff shl bitIndex
            val secondMask = 0xff shr (8 - bitIndex)
            data[byteIndex] = ((data[byteIndex].toInt() and firstMask) or (asInt shr (8 - bitIndex))).toByte()
            data[byteIndex + 1] = ((data[byteIndex + 1].toInt() and secondMask) or (asInt shl bitIndex)).toByte()
        }
        writerIndex += 8
    }

    fun readBits(bits: Int): Int {
        if (readAvailable() < bits) throw IndexOutOfBoundsException()
        if (bits > 32) throw IllegalArgumentException()
        var result = 0
        var remaining = bits
        var byteIndex = byteIndex(readerIndex)
        var bitIndex = bitIndex(readerIndex)
        if (bitIndex != 0 && remaining >= 8 - bitIndex) {
            result = (data[byteIndex++].toInt() and 0xff) and (0xff shr bitIndex)
            //println(String.format("%0${8 - bitIndex}d from first byte", result.toString(2).toInt()))
            remaining -= 8 - bitIndex
            bitIndex = 0
        }
        assert(bitIndex == 0 || remaining < 8)
        while (remaining >= 8) {
            result = (result shl 8) or (data[byteIndex++].toInt() and 0xff)
            //println(String.format("%08d from middle", (result and 0xff).toString(2).toInt()))
            remaining -= 8
        }
        if (remaining > 0) {
            val mask = (1 shl remaining) - 1
            result = (result shl remaining) or (((data[byteIndex].toInt() and 0xff) shr (8 - bitIndex - remaining)) and mask)
            //println(String.format("%0${remaining}d from last", (result and mask).toString(2).toInt()))
        }
        readerIndex += bits
        //println(String.format("result: %0${bits}d", result.toString(2).toInt()))
        return result
    }

    companion object {
        fun byteIndex(index: Int) = index shr 3
        fun bitIndex(index: Int) = index and 7
    }
}