package de.skyrising.aoc

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.net.URL
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

val inputs: Int2ObjectMap<Int2ObjectMap<ByteBuffer>> = Int2ObjectOpenHashMap()

fun getInput(year: Int, day: Int): ByteBuffer = inputs.computeIfAbsent(year, Int2ObjectFunction {
    Int2ObjectOpenHashMap()
}).computeIfAbsent(day, Int2ObjectFunction {
    getInput0(year, it)
})

private fun getInput0(year: Int, day: Int): ByteBuffer {
    val connection = URL("https://adventofcode.com/${year}/day/$day/input").openConnection()
    connection.addRequestProperty("Cookie", System.getenv("AOC_COOKIE"))
    return ByteBuffer.wrap(connection.getInputStream().readBytes()).asReadOnlyBuffer()
}

fun lineList(buf: ByteBuffer): List<ByteBuffer> {
    var lineStart = 0
    var r = false
    val lines = mutableListOf<ByteBuffer>()
    for (i in 0 until buf.limit()) {
        val b = buf[i]
        if (b == '\n'.toByte()) {
            buf.position(lineStart)
            buf.limit(i - if (r) 1 else 0)
            lines.add(buf.slice())
            buf.clear()
            lineStart = i + 1
        }
        r = b == '\r'.toByte()
    }
    if (lineStart < buf.limit()) {
        buf.position(lineStart)
        lines.add(buf.slice())
    }
    return lines
}

// Poor man's JMH

private var blackhole: Unit? = Unit
fun blackhole(o: Any?) {
    blackhole = if (o == null || o != blackhole) Unit else blackhole
}

fun <T> measure(runs: Int, fn: () -> T?): Double {
    val start = System.nanoTime()
    repeat(runs) {
        blackhole(fn())
    }
    return (System.nanoTime() - start) / (1000.0 * runs)
}

fun isBitSet(longs: LongArray, i: Int): Boolean {
    return (longs[i shr 6] shr (i and 0x3f)) and 1 != 0L
}

fun setBit(longs: LongArray, i: Int) {
    val idx = i shr 6
    longs[idx] = longs[idx] or (1L shl (i and 0x3f))
}

fun setBit(longs: LongArray, i: Int, value: Boolean) {
    val idx = i shr 6
    val bits = (1L shl (i and 0x3f))
    if (value) {
        longs[idx] = longs[idx] or bits
    } else {
        longs[idx] = longs[idx] and bits.inv()
    }
}

inline fun splitToRanges(s: CharBuffer, delimiter: Char, consumer: CharBuffer.(from: Int, to: Int) -> Unit) {
    val len = s.length
    var offset = 0
    while (offset < len) {
        val next = indexOfOrLength(s, delimiter, offset, len)
        consumer(s, offset, next)
        if (next == len) return
        offset = next + 1
    }
}

fun indexOfOrLength(chars: CharBuffer, delimiter: Char, offset: Int, len: Int): Int {
    val pos = chars.position()
    for (i in pos + offset until pos + len) {
        if (chars.get(i) == delimiter) return i - pos
    }
    return len
}

fun CharBuffer.positionAfter(delimiter: Char): Boolean {
    for (i in position() until limit()) {
        if (this.get(i) == delimiter) {
            this.position(i + 1)
            return true
        }
    }
    return false
}

fun CharBuffer.until(delimiter: Char): Boolean {
    for (i in position() until limit()) {
        if (this.get(i) == delimiter) {
            this.limit(i)
            return true
        }
    }
    return false
}

fun ByteBuffer.positionAfter(delimiter: Byte): Boolean {
    for (i in position() until limit()) {
        if (this.get(i) == delimiter) {
            this.position(i + 1)
            return true
        }
    }
    return false
}

fun ByteBuffer.until(delimiter: Byte): Boolean {
    for (i in position() until limit()) {
        if (this.get(i) == delimiter) {
            this.limit(i)
            return true
        }
    }
    return false
}

fun <T : Buffer> T.unflip(): T {
    position(limit())
    limit(capacity())
    return this
}

fun <T : Buffer> T.inc(amount: Int = 1): T {
    position(position() + amount)
    return this
}

fun ByteBuffer.toString(charset: Charset) = charset.decode(slice()).toString()

class MutableBox<T>(var value: T)