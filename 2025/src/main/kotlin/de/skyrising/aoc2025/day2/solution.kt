@file:PuzzleName("Gift Shop")

package de.skyrising.aoc2025.day2

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import jdk.incubator.vector.ByteVector
import jdk.incubator.vector.VectorShuffle

val test = TestInput("""
    11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
    1698522-1698528,446443-446449,38593856-38593862,565653-565659,
    824824821-824824827,2121212118-2121212124
""".replace(Regex("\\s"), ""))

fun PuzzleInput.prepare() = string.trim().split(',').map { it.toLongRange() }

inline fun List<LongRange>.sumInvalid(crossinline lengthFilter: (Int)-> Boolean = { true }, crossinline predicate: (ByteView) -> Boolean): Long {
    return mapParallel {
        var sum = 0L
        val bytes = ByteArray(64)
        val cs = ByteView(bytes, 20, 20)
        var element = it.first
        val endExclusive = it.last + 1
        var nextLengthCheck = element
        while (element < endExclusive) {
            if (element >= nextLengthCheck) {
                val len = element.stringSize()
                if (!lengthFilter(len)) {
                    element = 10L pow len
                    nextLengthCheck = element * 10
                    continue
                } else {
                    nextLengthCheck = 10L pow len
                }
            }
            cs.start = element.getChars(bytes, 20)
            if (predicate(cs)) sum += element
            element++
        }
        sum
    }.sum()
}

fun List<LongRange>.part1naive() = sumInvalid({ it % 2 == 0 }) { s ->
    val l = s.length / 2
    for (i in 0 ..< l) if (s[i] != s[l + i]) return@sumInvalid false
    true
}

fun Long.stringRepeat(n: Int): Long {
    val p10 = 10L pow stringSize()
    var p = 1L
    repeat(n-1) {
        p = p10 * p + 1
    }
    return this * p
}

fun List<LongRange>.part1() = sumOf { r ->
    var subTotal = 0L
    val start = r.first.toString()
    val lstart = start.length
    val end = r.last.toString()
    val lend = end.length
    if (lstart % 2 == 1 && lend == lstart) return@sumOf 0
    if (lend > lstart + 1) throw UnsupportedOperationException("Unhandled range $r")
    val startHalf = if (lstart < 2) 0L else start.take(lstart / 2).toLong()
    val endHalf = end.take(lend / 2 + lend % 2).toLong()
    for (idHalf in startHalf..endHalf) {
        val id = idHalf.stringRepeat(2)
        if (id in r) subTotal += id
    }
    subTotal
}

fun List<LongRange>.part2naive() = sumInvalid { s ->
    val len = s.length
    outer@for (start in 1 ..< len) {
        for (i in 0 ..< len) {
            var j = start + i
            if (j >= len) j -= len
            if (s[i] != s[j]) continue@outer
        }
        return@sumInvalid true
    }
    return@sumInvalid false
}

private fun makeSetupShuffle(len: Int): Any {
    val values = IntArray(32) { 31 }
    var i = 0
    var j = 1
    while (i < len - 1) values[i++] = j++
    j = 0
    while (i < len * 2 - 2) values[i++] = j++
    return VectorShuffle.fromArray(ByteVector.SPECIES_256, values, 0)
}


fun List<LongRange>.part2vec(): Long {
    val SETUP_SHUFFLE = Array(16, ::makeSetupShuffle)
    return sumInvalid { s ->
        val len = s.length
        val vec = ByteVector.fromArray(ByteVector.SPECIES_256, s.bytes, 0).slice(s.start)
        val setup = vec.rearrange(SETUP_SHUFFLE[len] as VectorShuffle<Byte>)
        val goal = (1 shl len) - 1
        var result = false
        repeat(len / 2) {
            result = result or (vec.eq(setup.slice(it)).toLong().toInt() and goal == goal)
        }
        result
    }
}

fun List<LongRange>.part2(): Long {
    var total = 0L
    val dups = LongOpenHashSet(144)
    for (r in this) {
        val start = r.first.toString()
        val lstart = start.length
        val end = r.last.toString()
        val lend = end.length
        if (lend > lstart + 1) throw UnsupportedOperationException("Unhandled range $r")
        for (l in 0..lstart / 2) {
            if (l == 0 && lstart == lend) continue
            val startChunk = if (l == 0) 0L else start.take(l).toLong()
            val endChunk = end.take(l + lend - lstart).toLong()
            for (idPartial in startChunk..endChunk) {
                val pLen = idPartial.stringSize()
                val repStart = lstart / pLen
                val id = idPartial.stringRepeat(repStart)
                if (id >= 9 && id in r && dups.add(id)) {
                    total += id
                } else if (lend != lstart && lend / pLen != repStart) {
                    val id = idPartial.stringRepeat(lend / pLen)
                    if (id in r && dups.add(id)) total += id
                }
            }
        }
        dups.clear()
    }
    return total
}