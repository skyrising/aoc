@file:PuzzleName("Race Condition")

package de.skyrising.aoc2024.day20

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList
import kotlin.math.absoluteValue

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

fun walk(grid: CharGrid): Pair<IntGrid, LongList> {
    val start = grid.indexOfFirst { it == 'S' }
    val end = grid.indexOfFirst { it == 'E' }
    val time = IntGrid(grid.width, grid.height) { _, _ -> Int.MAX_VALUE }
    var pos = start
    var steps = 0
    val path = LongArrayList()
    while (pos != end) {
        time[pos] = steps++
        path.add(pos.longValue)
        for (next in pos.fourNeighbors()) {
            if (next !in grid || grid[next] == '#' || time[next] < Int.MAX_VALUE) continue
            pos = next
        }
    }
    time[pos] = steps
    path.add(pos.longValue)
    return time to path
}

fun IntGrid.checkCheat(fromX: Int, fromY: Int, deltaX: Int, deltaY: Int): Int {
    val toX = fromX + deltaX
    val toY = fromY + deltaY
    if (!contains(toX, toY)) return 0
    val toValue = this[toX, toY]
    if (toValue == Int.MAX_VALUE) return 0
    return ((this[fromX, fromY] - toValue).absoluteValue - deltaX.absoluteValue - deltaY.absoluteValue >= 100).toInt()
}

fun PuzzleInput.part1(): Int {
    val time = walk(charGrid).first
    var count = 0
    time.forEach { x, y, steps ->
        if (steps == Int.MAX_VALUE) return@forEach
        count += time.checkCheat(x, y, 2, 0)
        count += time.checkCheat(x, y, 0, 2)
    }
    return count
}

fun PuzzleInput.part2(): Any {
    val (time, posList) = walk(charGrid)
    val size = posList.size
    val chunkSize = if (benchmark) 100 else Int.MAX_VALUE
    return (0 until size step chunkSize).mapParallel { startI ->
        var localCount = 0
        for (i in startI until minOf(startI + chunkSize, size)) {
            val posL = posList.getLong(i)
            val posX = unpackFirstInt(posL)
            val posY = unpackSecondInt(posL)
            for (x in 2..20) {
                localCount += time.checkCheat(posX, posY, x, 0)
            }
            for (y in 1..20) {
                for (x in (y-20)..(20-y)) {
                    localCount += time.checkCheat(posX, posY, x, y)
                }
            }
        }
        localCount
    }.sum()
}
