package de.skyrising.aoc2021

import de.skyrising.aoc.Vec2i
import it.unimi.dsi.fastutil.objects.Object2CharMap
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap

class BenchmarkDay25 : BenchmarkDayV1(25)

fun registerDay25() {
    val test = listOf(
        "v...>>.vv>",
        ".vv>>.vv..",
        ">>.>v>...v",
        ">>v>>.>.v.",
        "v>v.vv.v..",
        ">.>>..v...",
        ".vv..>.>v.",
        "v.v..>>v.v",
        "....v..v.>"
    )
    puzzleLS(25, "Sea Cucumber") {
        val (map, size) = parseInput25(it)
        var state = map
        var steps = 0
        while (true) {
            steps++
            val newState = step(state, size)
            if (state == newState) break
            state = newState
        }
        steps
    }
}

private fun step(map: Object2CharMap<Vec2i>, size: Vec2i): Object2CharMap<Vec2i> {
    val newMap = Object2CharOpenHashMap<Vec2i>()
    for (e in map.object2CharEntrySet()) {
        if (e.charValue != '>') continue
        val (x, y) = e.key
        val destPos = Vec2i((x + 1) % size.x, y)
        if (!map.containsKey(destPos)) {
            newMap[destPos] = '>'
        } else {
            newMap[e.key] = '>'
        }
    }
    for (e in map.object2CharEntrySet()) {
        if (e.charValue != 'v') continue
        val (x, y) = e.key
        val destPos = Vec2i(x, (y + 1) % size.y)
        if (!newMap.containsKey(destPos) && map.getChar(destPos) != 'v') {
            newMap[destPos] = 'v'
        } else {
            newMap[e.key] = 'v'
        }
    }
    return newMap
}

private fun show(map: Object2CharMap<Vec2i>, size: Vec2i): String {
    val sb = StringBuilder()
    for (y in 0 until size.y) {
        if (y > 0) sb.append('\n')
        for (x in 0 until size.x) {
            sb.append(map.getOrDefault(Vec2i(x, y) as Any, '.'))
        }
    }
    return sb.toString()
}

private fun parseInput25(input: List<String>): Pair<Object2CharMap<Vec2i>, Vec2i> {
    val map = Object2CharOpenHashMap<Vec2i>()
    for ((y, line) in input.withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == '.') continue
            map[Vec2i(x, y)] = c
        }
    }
    return map to Vec2i(input[0].length, input.size)
}