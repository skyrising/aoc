package de.skyrising.aoc2024.day11

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.longs
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.util.function.LongFunction

val test = TestInput("""
125 17
""")

fun Long2ObjectMap<LongArray>.memo(stone: Long): LongArray = computeIfAbsent(stone, LongFunction {
    if (stone == 0L) longArrayOf(1)
    else {
        val s = stone.toString()
        if (s.length and 1 == 0) longArrayOf(
            s.substring(0, s.length / 2).toLong(),
            s.substring(s.length / 2).toLong()
        ) else longArrayOf(stone * 2024)
    }
})

fun Long2ObjectMap<LongArray>.memo(next: Long2ObjectMap<LongArray>, stone: Long, blinks: Int, maxBlinks: Int = blinks + 1): Long {
    val subMap = get(stone) ?: LongArray(maxBlinks).also { put(stone, it) }
    val oldValue = subMap[blinks]
    if (oldValue != 0L) return oldValue
    val value = if (blinks == 0) 1 else {
        var sum = 0L
        val n = next.memo(stone)
        for (i in n.indices) {
            sum += memo(next, n[i], blinks - 1, maxBlinks)
        }
        sum
    }
    subMap[blinks] = value
    return value
}

@PuzzleName("Plutonian Pebbles")
fun PuzzleInput.part1(): Any {
    val next = Long2ObjectOpenHashMap<LongArray>()
    val sums = Long2ObjectOpenHashMap<LongArray>()
    return chars.longs().sumOf { sums.memo(next, it, 25) }
}

fun PuzzleInput.part2(): Any {
    val next = Long2ObjectOpenHashMap<LongArray>()
    val sums = Long2ObjectOpenHashMap<LongArray>()
    return chars.longs().sumOf {
        sums.memo(next, it, 75)
    }
}
