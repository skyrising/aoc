@file:PuzzleName("Reactor")

package de.skyrising.aoc2025.day11

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.forEachInt
import it.unimi.dsi.fastutil.ints.*

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

fun Int2ObjectMap<IntSet>.countPaths(from: Int, to: Int, cache: Int2LongMap = Int2LongOpenHashMap(size)): Long {
    if (from == to) return 1
    val known = cache.getOrDefault(to, -1)
    if (known != -1L) return known
    var count = 0L
    val incoming = this[to]
    if (incoming != null) {
        incoming.forEachInt {
            count += countPaths(from, it, cache)
        }
    }
    cache.put(to, count)
    return count
}

fun PuzzleInput.prepare(): Int2ObjectMap<IntSet> {
    val lines = byteLines
    val incoming = Int2ObjectOpenHashMap<IntSet>(lines.size)
    for (line in lines) {
        val from = line.getInt(0) shr 8
        val limit = line.limit()
        var i = 4
        while (i < limit) {
            val to = line.getInt(i) and 0xffffff
            var set = incoming[to]
            if (set == null) {
                set = IntArraySet()
                incoming.put(to, set)
            }
            set.add(from)
            i += 4
        }
    }
    return incoming
}

const val YOU = 'y'.code.shl(16) or 'o'.code.shl(8) or 'u'.code
const val OUT = 'o'.code.shl(16) or 'u'.code.shl(8) or 't'.code
const val SVR = 's'.code.shl(16) or 'v'.code.shl(8) or 'r'.code
const val DAC = 'd'.code.shl(16) or 'a'.code.shl(8) or 'c'.code
const val FFT = 'f'.code.shl(16) or 'f'.code.shl(8) or 't'.code

fun Int2ObjectMap<IntSet>.part1() = countPaths(YOU, OUT)

fun Int2ObjectMap<IntSet>.part2(): Long {
    val dacFft = countPaths(DAC, FFT)
    return if (dacFft != 0L) {
        countPaths(SVR, DAC) * dacFft * countPaths(FFT, OUT)
    } else {
        countPaths(SVR, FFT) * countPaths(FFT, DAC) * countPaths(DAC, OUT)
    }
}
