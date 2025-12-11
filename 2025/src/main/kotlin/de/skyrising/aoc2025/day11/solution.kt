@file:PuzzleName("Reactor")
@file:Suppress("NOTHING_TO_INLINE")

package de.skyrising.aoc2025.day11

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.toIntExact
import it.unimi.dsi.fastutil.shorts.Short2ShortFunction
import it.unimi.dsi.fastutil.shorts.Short2ShortOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortArrayList
import it.unimi.dsi.fastutil.shorts.ShortList

val test = TestInput("""
aaa: you hhh
you: bbb ccc
bbb: ddd eee
ccc: ddd eee fff
ddd: ggg
eee: out
fff: out
ggg: out
hhh: ccc fff iii
iii: out
""")

val test2 = TestInput("""
svr: aaa bbb
aaa: fft
fft: ccc
bbb: tty
tty: ccc
ccc: ddd eee
ddd: hub
hub: fff
eee: dac
dac: fff
fff: ggg hhh
ggg: out
hhh: out
""")

fun Prepared.countPaths(from: Short, to: Short, cache: IntArray = IntArray(size) { -1 }): Int {
    if (from == to) return 1
    val known = cache[to.toInt()]
    if (known != -1) return known
    var count = 0L
    val incoming = incoming[to.toInt()]
    for (i in incoming.indices) {
        count += countPaths(from, incoming.getShort(i), cache)
    }
    cache[to.toInt()] = count.toIntExact()
    return count.toInt()
}

class Prepared(
    val incoming: Array<ShortList>,
    val size: Int,
    val you: Short,
    val out: Short,
    val svr: Short,
    val dac: Short,
    val fft: Short,
)

inline fun compress(value: Int): Short {
    val a = value.shr(16).and(31)
    val b = value.shr(8).and(31)
    val c = value.and(31)
    return (a.shl(10) or b.shl(5) or c).toShort()
}

inline fun Short2ShortFunction.id(value: Int): Short {
    val key = compress(value)
    var i = getOrDefault(key, -1)
    if (i < 0) {
        i = size().toShort()
        put(key, i)
    }
    return i
}

fun PuzzleInput.prepare(): Prepared {
    val lines = byteLines
    val ids = Short2ShortOpenHashMap(lines.size + 1)
    val incoming = Array<ShortList>(lines.size + 1) { ShortArrayList(23) }
    for (line in lines) {
        val from = ids.id(line.getInt(0) shr 8)
        for (i in 1 ..< line.limit() / 4) {
            val to = ids.id(line.getInt(i * 4) and 0xffffff)
            incoming[to.toInt()].add(from)
        }
    }
    return Prepared(
        incoming,
        ids.size,
        ids.id('y'.code.shl(16) or 'o'.code.shl(8) or 'u'.code),
        ids.id('o'.code.shl(16) or 'u'.code.shl(8) or 't'.code),
        ids.id('s'.code.shl(16) or 'v'.code.shl(8) or 'r'.code),
        ids.id('d'.code.shl(16) or 'a'.code.shl(8) or 'c'.code),
        ids.id('f'.code.shl(16) or 'f'.code.shl(8) or 't'.code),
    )
}

fun Prepared.part1() = countPaths(you, out)

fun Prepared.part2(): Long {
    val fftDac = countPaths(fft, dac)
    return if (fftDac != 0) {
        countPaths(svr, fft).toLong() * fftDac * countPaths(dac, out)
    } else {
        countPaths(svr, dac).toLong() * countPaths(dac, fft) * countPaths(fft, out)
    }
}
