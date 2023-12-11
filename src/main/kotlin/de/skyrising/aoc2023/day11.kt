package de.skyrising.aoc2023

import de.skyrising.aoc.CharGrid
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.chars.CharArrayList
import it.unimi.dsi.fastutil.ints.IntArrayList

@Suppress("unused")
class BenchmarkDay11 : BenchmarkDayV1(11)

@Suppress("unused")
fun registerDay11() {
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
    part1("Cosmic Expansion") {
        val input = this
        val origGrid = input.charGrid
        val emptyColumns = IntArrayList()
        for (x in 0 until origGrid.width) {
            var empty = true
            for (y in 0 until origGrid.height) {
                if (origGrid[x, y] == '#') {
                    empty = false
                    break
                }
            }
            if (empty) emptyColumns.add(x)
        }
        emptyColumns.reverse()
        val expanded = input.lines.flatMap {
            val chars = CharArrayList(it.toCharArray())
            for (x in emptyColumns) {
                chars.add(x, '.')
            }
            val s = String(chars.toCharArray())
            if (s.contains("#")) listOf(s) else listOf(s, s)
        }
        val grid = CharGrid.parse(expanded)
        val galaxies = grid.where { it == '#' }
        var sum = 0
        for (i in galaxies.indices) {
            for (j in i + 1..galaxies.lastIndex) {
                val a = galaxies[i]
                val b = galaxies[j]
                sum += a.manhattanDistance(b)
            }
        }
        sum
    }
    part2 {
        val input = this
        val scale = 1000000
        val grid = input.charGrid
        val countRow = IntArray(grid.height)
        val countCol = IntArray(grid.width)
        grid.forEach { x, y, c ->
            if (c == '#') {
                countRow[y]++
                countCol[x]++
            }
        }
        val galaxies = grid.where { it == '#' }
        var sum = 0L
        for (i in galaxies.indices) {
            for (j in i + 1..galaxies.lastIndex) {
                val a = galaxies[i]
                val b = galaxies[j]
                for (y in minOf(a.y, b.y) until maxOf(a.y, b.y)) {
                    sum += if (countRow[y] == 0) scale else 1
                }
                for (x in minOf(a.x, b.x)until maxOf(a.x, b.x)) {
                    sum += if (countRow[x] == 0) scale else 1
                }
            }
        }
        sum
    }
}