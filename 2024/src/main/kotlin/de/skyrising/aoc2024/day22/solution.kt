package de.skyrising.aoc2024.day22

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import jdk.incubator.vector.IntVector
import jdk.incubator.vector.VectorOperators

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

@PuzzleName("Monkey Market")
fun PuzzleInput.part1() = lines.sumOf {
    var num = it.toLong()
    repeat(2000) {
        num = next(num)
    }
    num
}

private val SPECIES = IntVector.SPECIES_PREFERRED

@PuzzleName("Monkey Market")
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
    val count = Int2IntOpenHashMap()
    fun pack(a: Int, b: Int, c: Int, d: Int) = (a shl 24) or ((b and 0xff) shl 16) or ((c and 0xff) shl 8) or (d and 0xff)
    fun unpack(n: Int) = listOf((n shr 24).toByte().toInt(), (n shr 16).toByte().toInt(), (n shr 8).toByte().toInt(), n.toByte().toInt())
    val history = IntArrayDeque()
    var maxCount = 0
    for (init in lines) {
        history.clear()
        var num = init.toLong()
        val seen = IntOpenHashSet(2000)
        repeat(2001) {
            val n = next(num)
            history.enqueue((n % 10 - num % 10).toInt())
            while (history.size() > 4) {
                val packed = pack(history[0], history[1], history[2], history[3])
                if (seen.add(packed)) {
                    val oldCount = count.addTo(packed, (num % 10).toInt())
                    val newCount = oldCount + (num % 10).toInt()
                    if (newCount > maxCount) {
                        maxCount = newCount
                    }
                }
                history.dequeueInt()
            }
            num = n
        }
    }
    return maxCount
}
