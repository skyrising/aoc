package de.skyrising.aoc2024.day11

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.longs
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

val test = TestInput("""
125 17
""")

@JvmInline
value class Stones(val value: Long) {
    val hasTwo: Boolean get() = value < 0
    val first: Long get() = if (value < 0) (value shr 32 and 0x7fffffff) else value
    val second: Long get() = if (value < 0) (value and 0xffffffff) else 0
    constructor(a: Int, b: Int) : this((1L shl 63) or (a.toLong() shl 32) or b.toLong())

    override fun toString() = if (hasTwo) "($first, $second)" else "($first)"
}

fun getNextStones(stone: Long): Stones {
    if (stone == 0L) return Stones(1L)
    var i = 1L
    while (i < Int.MAX_VALUE / 10) {
        val div = i * 10
        val sq = i * div
        if (sq > stone) break
        if (stone <= minOf(10*sq-1, Long.MAX_VALUE)) {
            val a = (stone / div).toInt()
            val b = (stone - a * div).toInt()
            return Stones(a, b)
        }
        i *= 10
    }
    return Stones(stone * 2024)
}

private const val SIZE = 1 shl 11
class SumStorage(val maxBlinks: Int) {
    private val array = LongArray(SIZE * (maxBlinks + 1))
    val map = Long2ObjectOpenHashMap<LongArray>(36 * maxBlinks) // 25->31, 75->36, max@45->53

    fun memo(stone: Long, blinks: Int = maxBlinks): Long {
        val index = when {
            stone < SIZE / 2 -> stone
            stone % 2024L == 0L -> SIZE / 2 + (stone / 2024L)
            else -> -stone
        }
        var arrayIndex = blinks
        val subMap = if (index in 0L until SIZE) {
            arrayIndex = blinks * SIZE + index.toInt()
            array
        } else {
            map[index] ?: LongArray(maxBlinks + 1).also { map.put(index, it) }
        }
        val oldValue = subMap[arrayIndex]
        if (oldValue != 0L) return oldValue
        val value = if (blinks == 0) 1 else {
            val n = getNextStones(stone)
            var sum = memo(n.first, blinks - 1)
            if (n.hasTwo) {
                sum += memo(n.second, blinks - 1)
            }
            sum
        }
        subMap[arrayIndex] = value
        return value
    }
}

@PuzzleName("Plutonian Pebbles")
fun PuzzleInput.part1(): Any {
    val sums = SumStorage(25)
    return chars.longs().sumOf { sums.memo(it) }
}

fun PuzzleInput.part2(): Any {
    val sums = SumStorage(75)
    return chars.longs().sumOf { sums.memo(it) }
}
