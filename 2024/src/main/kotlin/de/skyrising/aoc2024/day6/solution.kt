@file:PuzzleName("Guard Gallivant")

package de.skyrising.aoc2024.day6

import de.skyrising.aoc.*
import java.util.*
import java.util.concurrent.StructuredTaskScope
import java.util.concurrent.atomic.AtomicInteger

val test = TestInput("""
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
""")

inline fun guardWalk(grid: CharGrid, startPos: Vec2i, startDir: Direction, blocker: Vec2i? = null, cb: (Int, Int) -> Unit = { _, _ -> }): Boolean {
    var dir = startDir
    var x = startPos.x
    var y = startPos.y
    val seen = BitSet(grid.width * grid.height * 4)
    while (true) {
        cb(x, y)
        val idx = grid.localIndex(x, y) * 4 + dir.ordinal
        if (seen.get(idx)) {
            return true
        }
        seen.set(idx)
        var nextX = x + dir.x
        var nextY = y + dir.y
        if (!grid.contains(nextX, nextY)) break
        while ((blocker != null && nextX == blocker.x && nextY == blocker.y) || grid[nextX, nextY] == '#') {
            dir = dir.rotateCW()
            nextX = x + dir.x
            nextY = y + dir.y
        }
        x = nextX
        y = nextY
    }
    return false
}

fun PuzzleInput.part1(): Any {
    val grid = charGrid
    val startPos = grid.indexOfFirst("^v<>"::contains)
    val startDir = Direction("^>v<".indexOf(grid[startPos]))
    guardWalk(grid, startPos, startDir) { x, y ->
        grid[x, y] = 'X'
    }
    return grid.count { it == 'X' }
}

fun PuzzleInput.part2(): Any {
    val grid = charGrid
    val startPos = grid.indexOfFirst("^v<>"::contains)
    val startDir = Direction("^>v<".indexOf(grid[startPos]))
    val guard = buildSet {
        guardWalk(grid, startPos, startDir) { x, y -> add(Vec2i(x, y)) }
    }.toList()
    val count = AtomicInteger()
    StructuredTaskScope.ShutdownOnFailure().use { scope ->
        val step = 100
        for (startI in 1..<guard.size step step) {
            scope.fork {
                for (i in startI until minOf(startI + step, guard.size)) {
                    if (grid[guard[i]] in "^v<>") continue
                    if (guardWalk(grid, startPos, startDir, guard[i])) {
                        count.incrementAndGet()
                    }
                }
            }
        }
        scope.join()
    }
    return count.get()
}
