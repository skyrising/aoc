@file:PuzzleName("Garden Groups")

package de.skyrising.aoc2024.day12

import de.skyrising.aoc.*

val test = TestInput("""
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE
""")

val test4 = TestInput("""
AAAA
BBCD
BBCC
EEEC
""")

val test5 = TestInput("""
EEEEE
EXXXX
EEEEE
EXXXX
EEEEE
""")

val test6 = TestInput("""
AAAAAA
AAABBA
AAABBA
ABBAAA
ABBAAA
AAAAAA
""")

typealias Prepared = Pair<CharGrid, List<Set<Vec2i>>>

fun CharGrid.regions(): List<Set<Vec2i>> {
    val regions = mutableListOf<Set<Vec2i>>()
    val visited = mutableSetOf<Vec2i>()
    forEach { x, y, c ->
        if (visited.add(Vec2i(x, y))) {
            val members = floodFill(Vec2i(x, y)) { p -> p.fourNeighbors().filter { it in this && this[it] == c } }
            visited.addAll(members)
            regions.add(members)
        }
    }
    return regions
}

fun PuzzleInput.prepare(): Prepared {
    val grid = charGrid
    return grid to grid.regions()
}

fun Prepared.part1(): Any {
    val (grid, regions) = this
    val fencing = IntArray(regions.size) {
        regions[it].sumOf { p ->
            val c = grid[p]
            p.fourNeighbors().count { n -> n !in grid || grid[n] != c }
        }
    }
    return regions.indices.sumOf { fencing[it] * regions[it].size }
}

data class Side(val start: Vec2i, val end: Vec2i, val direction: Direction) {
    fun isSameSide(next: Vec2i, d: Direction) = d == direction && if (direction.x != 0) {
        next.x == start.x && (next.y == start.y - 1 || next.y == end.y + 1)
    } else {
        next.y == start.y && (next.x == start.x - 1 || next.x == end.x + 1)
    }

    fun with(next: Vec2i): Side = if (direction.x != 0) {
        if (next.y == end.y + 1) Side(start, next, direction) else Side(next, end, direction)
    } else {
        if (next.x == end.x + 1) Side(start, next, direction) else Side(next, end, direction)
    }
}

fun CharGrid.sideBasedPrice(regions: List<Set<Vec2i>>): Int {
    return regions.sumOf { r ->
        // crucial to not split up some sides when the region has holes
        val region = r.sortedWith(compareBy({ it.y }, { it.x }))
        val sides = mutableSetOf<Side>()
        for (pos in region) {
            val c = this[pos]
            for (dir in 0..3) {
                val n = pos + Direction(dir)
                if (n !in this || this[n] != c) {
                    val side = sides.find { it.isSameSide(n, Direction(dir)) }
                    if (side != null) {
                        sides.remove(side)
                        sides.add(side.with(n))
                    } else {
                        sides.add(Side(n, n, Direction(dir)))
                    }
                    continue
                }
            }
        }
        region.size * sides.size
    }
}

fun Prepared.part2() = first.sideBasedPrice(second)
