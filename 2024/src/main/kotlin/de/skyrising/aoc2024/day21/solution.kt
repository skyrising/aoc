@file:PuzzleName("Keypad Conundrum")

package de.skyrising.aoc2024.day21

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import kotlin.math.absoluteValue

val test = TestInput("""
029A
980A
179A
456A
379A
""")

fun verify(keypad: CharGrid, from: Vec2i, moves: String): Boolean {
    var pos = from
    for (move in moves) {
        pos = when (move) {
            '>' -> pos.east
            '<' -> pos.west
            '^' -> pos.north
            'v' -> pos.south
            else -> return true
        }
        if (keypad[pos] == ' ') return false
    }
    return true
}

fun moves(keypad: CharGrid, from: Vec2i, to: Vec2i): List<String> {
    val diff = to - from
    val xDir = if (diff.x > 0) '>' else '<'
    val xSteps = diff.x.absoluteValue
    val yDir = if (diff.y > 0) 'v' else '^'
    val ySteps = diff.y.absoluteValue
    val totalSteps = xSteps + ySteps
    val result = mutableListOf<String>()
    val pow2 = 1 shl totalSteps
    for (i in 0 until pow2) {
        if (i.countOneBits() != xSteps) continue
        val steps = buildString {
            var bit = 1
            while (bit < pow2) {
                val v = i and bit
                if (v != 0) append(xDir)
                else append(yDir)
                bit = bit shl 1
            }
            append('A')
        }
        if (verify(keypad, from, steps)) result.add(steps)
    }
    return result
}

fun buildMoveMap(controls: CharGrid): Int2ObjectMap<List<String>> {
    val map = Int2ObjectOpenHashMap<List<String>>(controls.data.size * controls.data.size)
    for (i in controls.data.indices) {
        val c1 = controls.data[i]
        if (c1 == ' ') continue
        val v1 = Vec2i(i % controls.width, i / controls.width)
        for (j in controls.data.indices) {
            val c2 = controls.data[j]
            if (c2 == ' ') continue
            val v2 = Vec2i(j % controls.width, j / controls.width)
            map[c1.code shl 16 or c2.code] = moves(controls, v1, v2)
        }
    }
    return map
}

fun MutableMap<Pair<String, Int>, Long>.findShortest(s: String, depth: Int, keypadMap: Int2ObjectMap<List<String>>?, controlMap: Int2ObjectMap<List<String>>): Long {
    val key = s to depth
    if (key in this) return this[key]!!

    val map = keypadMap ?: controlMap
    var length = 0L
    var prev = 'A'
    for (c in s) {
        val paths = map[prev.code shl 16 or c.code] ?: throw IllegalStateException("No path from $prev to $c in ${map.keys}")
        length += if (depth == 0) {
            paths.first().length.toLong()
        } else {
            paths.minOf { findShortest(it, depth - 1, null, controlMap) }
        }
        prev = c
    }
    this[key] = length
    return length
}

fun solve(codes: List<String>, robots: Int): Long {
    val keypad = CharGrid(3, 4, "789456123 0A".toCharArray())
    val controls = CharGrid(3, 2, " ^A<v>".toCharArray())
    val controlMap = buildMoveMap(controls)
    val keypadMap = buildMoveMap(keypad)
    val cache = mutableMapOf<Pair<String, Int>, Long>()
    return codes.sumOf { code ->
        code.substring(0, 3).toLong() * cache.findShortest(code, robots, keypadMap, controlMap)
    }
}

fun PuzzleInput.part1() = solve(lines, 2)
fun PuzzleInput.part2()= solve(lines, 25)
