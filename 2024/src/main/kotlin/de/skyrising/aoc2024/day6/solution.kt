package de.skyrising.aoc2024.day6

import de.skyrising.aoc.*

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

fun guardWalk(grid: CharGrid) = sequence {
    val (startPos, dirChar) = grid.first { it.charValue in "^v<>" }
    var dir = Direction("^>v<".indexOf(dirChar))
    var pos = startPos
    val seen = mutableSetOf<Pair<Vec2i, Direction>>()
    while (true) {
        yield(pos)
        if (!seen.add(pos to dir)) {
            yield(null)
            break
        }
        var next = pos + dir
        if (next !in grid) break
        while (grid[next] == '#') {
            dir = dir.rotateCW()
            next = pos + dir
        }
        pos = next
    }
}

@PuzzleName("Guard Gallivant")
fun PuzzleInput.part1(): Any {
    val grid = charGrid
    for (pos in guardWalk(grid)) {
        grid[pos!!] = 'X'
    }
    return grid.count { it == 'X' }
}

fun PuzzleInput.part2(): Any {
    val grid = charGrid
    val guard = guardWalk(grid).toSet().toList() as List<Vec2i>
    return (1 until guard.size).count { i ->
        val newGrid = charGrid
        if (newGrid[guard[i]] in "^v<>") return@count false
        newGrid[guard[i]] = '#'
        guardWalk(newGrid).last() == null
    }
}
