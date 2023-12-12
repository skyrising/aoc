package de.skyrising.aoc2023.day11

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 11)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """)
    fun sumDistances(grid: CharGrid, scale: Int): Long {
        val countRow = IntArray(grid.height)
        val countCol = IntArray(grid.width)
        val galaxies = grid.where { it == '#' }
        for (g in galaxies) {
            countRow[g.y]++
            countCol[g.x]++
        }
        var sum = 0L
        for (i in galaxies.indices) {
            val a = galaxies[i]
            for (j in i + 1..galaxies.lastIndex) {
                val b = galaxies[j]
                for (x in a.x minUntilMax b.x) sum += if (countRow[x] == 0) scale else 1
                for (y in a.y minUntilMax b.y) sum += if (countRow[y] == 0) scale else 1
            }
        }
        "".trim(Char::isWhitespace)
        return sum
    }
    part1("Cosmic Expansion".trim()) {
        sumDistances(charGrid, 1)
    }
    part2 {
        sumDistances(charGrid, 1000000)
    }
}