package de.skyrising.aoc2024.day20

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2IntMap
import java.util.concurrent.StructuredTaskScope
import kotlin.math.abs

val test = TestInput("""
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############
""")

fun walk(grid: CharGrid): Long2IntMap {
    val start = grid.indexOfFirst { it == 'S' }
    val end = grid.indexOfFirst { it == 'E' }
    val visited = Long2IntLinkedOpenHashMap()
    var pos = start
    var steps = 0
    while (pos != end) {
        visited[PackedIntPair.pack(pos)] = steps++
        for (next in pos.fourNeighbors()) {
            if (next !in grid || grid[next] == '#' || visited.containsKey(next.longValue)) continue
            pos = next
        }
    }
    visited[PackedIntPair.pack(pos)] = steps
    return visited
}

@PuzzleName("Race Condition")
fun PuzzleInput.part1(): Int {
    val grid = charGrid
    val walk = walk(grid)
    var count = 0
    for ((posL, steps) in walk) {
        val pos = Vec2i(posL)
        for (dir in Direction.values) {
            if (grid[pos + dir] != '#') continue
            val next = pos + dir * 2
            val nextSteps = walk[next.longValue]
            val saved = nextSteps - 2 - steps
            if (saved >= 100) count++
        }
    }
    return count
}

fun PuzzleInput.part2(): Int {
    val grid = charGrid
    val walk = walk(grid)
    val posList = LongArray(walk.size)
    val stepsList = IntArray(walk.size)
    for ((i, e) in walk.long2IntEntrySet().withIndex()) {
        val (pos, steps) = e
        posList[i] = pos
        stepsList[i] = steps
    }
    val size = walk.size
    return StructuredTaskScope.ShutdownOnFailure().use { scope ->
        val chunkSize = 100
        val tasks = (0 until size step chunkSize).mapParallel(scope) { startI ->
            var localCount = 0
            for (i in startI until minOf(startI + chunkSize, size)) {
                val pos1 = posList[i]
                val x1 = unpackFirstInt(pos1)
                val y1 = unpackSecondInt(pos1)
                val steps1 = stepsList[i]
                for (j in i + 100 until size) {
                    val pos2 = posList[j]
                    val x2 = unpackFirstInt(pos2)
                    val distX = x1 - x2
                    if (distX > 20 || distX < -20) continue
                    val y2 = unpackSecondInt(pos2)
                    val distY = y1 - y2
                    if (distY > 20 || distY < -20) continue
                    val dist = abs(distX) + abs(distY)
                    if (dist > 20) continue
                    val steps2 = stepsList[j]
                    val saved = steps2 - steps1 - dist
                    if (saved >= 100) localCount++
                }
            }
            localCount
        }
        scope.join()
        tasks.sumOf { it.get() }
    }
}

