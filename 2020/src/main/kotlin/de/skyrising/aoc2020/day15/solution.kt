@file:PuzzleName("Rambunctious Recitation")

package de.skyrising.aoc2020.day15

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.HashCommon
import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import kotlin.collections.List
import kotlin.collections.map
import kotlin.collections.set

private fun day15next(mem: Int2IntMap, turn: Int, next: Int): Int {
    val n = turn - mem.getOrDefault(next, turn)
    mem[next] = turn
    return n
}

private fun day15(starting: List<Int>, limit: Int): Int {
    val mem = Int2IntOpenHashMap(limit / 4)
    var turn = 0
    var next = 0
    for (n in starting) {
        next = turn - mem.getOrDefault(n, turn)
        mem[n] = turn++
        // println("$turn: $n")
    }
    while (turn < limit - 1) {
        // println("${turn+1}: $next")
        next = day15next(mem, turn++, next)
    }
    // println(mem.size)
    return next
}

private const val CACHE_SIZE = 64
private const val CACHE_MASK = (CACHE_SIZE - 1) shl 1

private fun day15v2(starting: List<Int>, limit: Int): Int {
    // val missTracker = Int2IntOpenHashMap()
    val mem = IntArray(limit)
    val cache = IntArray(CACHE_SIZE * 2) { -1 }
    val first = starting[0]
    var turn = 0
    var next = 0
    // var max = 0
    for (n in starting) {
        var last = mem[n]
        if (last == 0 && n != first) last = turn
        // max = maxOf(max, next)
        val idx = HashCommon.mix(n) and CACHE_MASK
        cache[idx] = n
        cache[idx + 1] = turn
        mem[n] = turn
        next = turn++ - last
        // println("$turn: $n")
    }
    // var misses = 0
    // var hits = 0
    while (turn < limit - 1) {
        // println("${turn+1}: $next")
        // max = maxOf(max, next)
        val idx = HashCommon.mix(next) and CACHE_MASK
        val cacheKey = cache[idx]
        val last = if (cacheKey == next) {
            cache[idx + 1]
        } else {
            val m = mem[next]
            if (m == 0 && next != first) turn else m
        }
        if (cacheKey != next) {
            // cache key changed, write to mem
            if (cacheKey >= 0) mem[cacheKey] = cache[idx + 1]
            cache[idx] = next
            // missTracker[next] = missTracker[next] + 1
            // misses++
        } /*else {
            hits++
        }*/
        // set cached value
        cache[idx + 1] = turn
        next = turn - last
        turn++
    }
    // println("$hits, $misses, ${misses * 100.0 / (hits + misses)}% miss rate")
    // println("${hashOr.toString(16)}, ${hashAnd.toString(16)}")
    // println(missTracker.int2IntEntrySet().stream().filter { it.intValue > 1 }.sorted(Comparator.comparingInt(Int2IntMap.Entry::getIntValue)).limit(20).toList())
    return next
}


val test = TestInput("0,3,6")

fun PuzzleInput.part1v0() = day15(chars.trim().split(",").map(String::toInt), 2020)

fun PuzzleInput.part1v1() = day15v2(chars.trim().split(",").map(String::toInt), 2020)

fun PuzzleInput.part2v0() = day15(chars.trim().split(",").map(String::toInt), 30000000)

fun PuzzleInput.part2v1() = day15v2(chars.trim().split(",").map(String::toInt), 30000000)
