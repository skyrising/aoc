package de.skyrising.aoc2022

import de.skyrising.aoc.*

class BenchmarkDay24 : BenchmarkDayV1(24)

private class Day24Setup(val start: Vec2i, val end: Vec2i, val grid: CharGrid, val blizzardsX: Array<Set<Vec2i>>, val blizzardsY: Array<Set<Vec2i>>) {
    fun validPos(pos: Vec2i) = pos in grid || pos == start || pos == end
    fun blizzardAt(pos: Vec2i, t: Int) = pos in blizzardsX[t % grid.width] || pos in blizzardsY[t % grid.height]
    fun dist(start: Vec2i, end: Vec2i, tStart: Int) = (bfsPath(start to tStart, { it.first == end }) { (pos, t) ->
        pos.fiveNeighbors().filter { validPos(it) && !blizzardAt(it, t + 1) }.map { it to t + 1 }
    } ?: error("No path found")).size - 1
}

private fun parseInput(input: PuzzleInput): Day24Setup {
    val grid = input.charGrid.translatedView(Vec2i(-1, -1))
    val innerGrid = grid.subGrid(Vec2i(0, 0), Vec2i(grid.width - 2, grid.height - 2))
    val empty = grid.where { it == '.' }
    val start = empty.first()
    val end = empty.last()
    val blizzardN = innerGrid.where { it == '^' }
    val blizzardE = innerGrid.where { it == '>' }
    val blizzardS = innerGrid.where { it == 'v' }
    val blizzardW = innerGrid.where { it == '<' }
    val blizzardsX = Array<Set<Vec2i>>(innerGrid.width) {
        val set = mutableSetOf<Vec2i>()
        for (b in blizzardE) set.add(Vec2i((b.x + it).mod(innerGrid.width), b.y))
        for (b in blizzardW) set.add(Vec2i((b.x - it).mod(innerGrid.width), b.y))
        set
    }
    val blizzardsY = Array<Set<Vec2i>>(innerGrid.height) {
        val set = mutableSetOf<Vec2i>()
        for (b in blizzardN) set.add(Vec2i(b.x, (b.y - it).mod(innerGrid.height)))
        for (b in blizzardS) set.add(Vec2i(b.x, (b.y + it).mod(innerGrid.height)))
        set
    }
    return Day24Setup(start, end, innerGrid, blizzardsX, blizzardsY)
}

fun registerDay24() {
    val test = TestInput("""
        #.######
        #>>.<^<#
        #.<..<<#
        #>v.><>#
        #<^v^^>#
        ######.#
    """)
    puzzle(24, "Blizzard Basin") {
        parseInput(this).run {
            dist(start, end, 0)
        }
    }
    puzzle(24, "Part Two") {
        parseInput(this).run {
            val a = dist(start, end, 0)
            val b = dist(end, start, a)
            val c = dist(start, end, a + b)
            log("$a+$b+$c")
            a + b + c
        }
    }
}