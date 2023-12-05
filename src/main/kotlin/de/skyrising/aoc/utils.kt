package de.skyrising.aoc

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset

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

fun String.ints(): IntList {
    val parts = split(Regex("[^0-9-]"))
    val ints = IntArrayList()
    for (part in parts) {
        if (part.isEmpty()) continue
        try {
            val i = part.toInt()
            ints.add(i)
        } catch (_: NumberFormatException) {}
    }
    return ints
}

fun String.longs(): LongList {
    val parts = split(Regex("[^0-9-]"))
    val ints = LongArrayList()
    for (part in parts) {
        if (part.isEmpty()) continue
        try {
            val i = part.toLong()
            ints.add(i)
        } catch (_: NumberFormatException) {}
    }
    return ints
}

fun <T> T.iterate(step: T.() -> T?): T {
    var current = this
    while (true) {
        val next = step(current) ?: return current
        current = next
    }
}

inline fun countWhile(predicate: (Int) -> Boolean): Int {
    var count = 0
    while (predicate(count)) {
        count++
    }
    return count
}

inline fun <T, C: MutableCollection<T>> Collection<T>.mergeTo(collection: C, merger: (T, T) -> T?): C {
    val it = iterator()
    if (!it.hasNext()) return collection
    var last = it.next()
    while (it.hasNext()) {
        val next = it.next()
        val merged = merger(last, next)
        last = if (merged != null) {
            merged
        } else {
            collection.add(last)
            next
        }
    }
    collection.add(last)
    return collection
}

fun joinRanges(ranges: Collection<IntRange>) = ranges.sortedBy { it.first }.mergeTo(mutableSetOf()) {
        a, b -> if (b.first <= a.last + 1) a.first..kotlin.math.max(a.last, b.last) else null
}

@JvmName("joinLongRanges")
fun joinRanges(ranges: Collection<LongRange>) = ranges.sortedBy { it.first }.mergeTo(mutableSetOf()) {
        a, b -> if (b.first <= a.last + 1) a.first..kotlin.math.max(a.last, b.last) else null
}

fun <T> Collection<T>.subsets(): Iterable<Set<T>> {
    val list = toList()
    return object : Iterable<Set<T>> {
        override fun iterator(): Iterator<Set<T>> {
            return object : Iterator<Set<T>> {
                var i = 0
                override fun hasNext() = i < 1 shl list.size
                override fun next(): Set<T> {
                    val set = mutableSetOf<T>()
                    for (j in list.indices) {
                        if (i and (1 shl j) != 0) {
                            set.add(list[j])
                        }
                    }
                    i++
                    return set
                }
            }
        }
    }
}

inline fun <T> floodFill(origin: T, step: (T) -> Collection<T>): Set<T> {
    val result = mutableSetOf<T>()
    val queue = ArrayDeque<T>()
    queue.add(origin)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current !in result) {
            result.add(current)
            queue.addAll(step(current))
        }
    }
    return result
}


operator fun <E> List<E>.component6() = this[5]
operator fun <E> List<E>.component7() = this[6]
operator fun <E> List<E>.component8() = this[7]
operator fun <E> List<E>.component9() = this[8]
operator fun <E> List<E>.component10() = this[9]
operator fun <E> List<E>.component11() = this[10]
operator fun <E> List<E>.component12() = this[11]
operator fun <E> List<E>.component13() = this[12]
operator fun <E> List<E>.component14() = this[13]
operator fun <E> List<E>.component15() = this[14]
operator fun <E> List<E>.component16() = this[15]