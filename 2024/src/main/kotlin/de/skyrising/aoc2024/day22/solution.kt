@file:PuzzleName("Monkey Market")

package de.skyrising.aoc2024.day22

import de.skyrising.aoc.*
import jdk.incubator.vector.IntVector
import jdk.incubator.vector.VectorOperators
import java.util.*

val test = TestInput("""
1
2
3
2024
""")

fun next(num: Long): Long {
    val a = (num xor (num shl 6)) and 0xffffff
    val b = (a xor (a ushr 5)) and 0xffffff
    return (b xor (b shl 11)) and 0xffffff
}

fun PuzzleInput.part1() = lines.sumOf {
    var num = it.toLong()
    repeat(2000) {
        num = next(num)
    }
    num
}

private val SPECIES = IntVector.SPECIES_PREFERRED

@Solution(SolutionType.C2)
fun PuzzleInput.part1vec(): Any {
    val l = lines
    val count = l.size
    val longs = IntArray(count) { l[it].toInt() }
    val mask24 = IntVector.fromArray(SPECIES, IntArray(SPECIES.length()) { 0xffffff }, 0)
    var sum = 0L
    for (i in 0 until count step SPECIES.length() * 2) {
        val mask1 = SPECIES.indexInRange(i, count)
        val mask2 = SPECIES.indexInRange(i + SPECIES.length(), count)
        var vec1 = IntVector.fromArray(SPECIES, longs, i, mask1)
        var vec2 = IntVector.fromArray(SPECIES, longs, i + SPECIES.length(), mask2)
        repeat(2000) {
            val a1 = (vec1 xor (vec1 shl 6)) and mask24
            val b1 = (a1 xor (a1 ushr 5)) and mask24
            vec1 = (b1 xor (b1 shl 11)) and mask24
            val a2 = (vec2 xor (vec2 shl 6)) and mask24
            val b2 = (a2 xor (a2 ushr 5)) and mask24
            vec2 = (b2 xor (b2 shl 11)) and mask24
        }
        sum += (vec1 + vec2).reduceLanes(VectorOperators.ADD).toLong()
    }
    return sum
}

fun PuzzleInput.part2(): Any {
    val count = ShortArray(19*19*19*19)
    var maxCount = 0
    for (init in lines) {
        var hist = 0
        var num = init.toLong()
        val seen = BitSet(count.size)
        repeat(2001) {
            val n = next(num)
            val delta = (n % 10 - num % 10).toInt()
            if (it > 3) {
                var packed = ((hist shr 24) and 0xff)
                packed = packed * 19 + ((hist shr 16) and 0xff)
                packed = packed * 19 + ((hist shr 8) and 0xff)
                packed = packed * 19 + (hist and 0xff)
                if (!seen.get(packed)) {
                    seen.set(packed)
                    val newCount = count[packed] + (num % 10).toInt()
                    count[packed] = newCount.toShort()
                    if (newCount > maxCount) {
                        maxCount = newCount
                    }
                }
            }
            hist = (hist shl 8) or (delta + 9)
            num = n
        }
    }
    return maxCount
}
