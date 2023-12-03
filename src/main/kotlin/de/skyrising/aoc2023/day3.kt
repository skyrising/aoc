package de.skyrising.aoc2023

import de.skyrising.aoc.CharGrid
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.Vec2i

class BenchmarkDay3 : BenchmarkDayV1(3)

fun registerDay3() {
    val test = TestInput("""
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...${'$'}.*....
        .664.598..
    """)
    fun findNumbers(grid: CharGrid, x: Int, y: Int, numbers: MutableMap<Vec2i, Int>): Set<Vec2i> {
        val found = mutableSetOf<Vec2i>()
        for (n in Vec2i(x, y).eightNeighbors()) {
            if (n !in grid || !grid[n].isDigit()) continue
            var start = n.x
            for (i in n.x downTo 0) {
                if (i == 0 || !grid[i - 1, n.y].isDigit()) {
                    start = i
                    break
                }
            }
            if (Vec2i(start, n.y) in numbers) {
                found.add(Vec2i(start, n.y))
                continue
            }
            var end = n.x
            for (i in n.x until grid.width) {
                if (i == grid.width - 1 || !grid[i + 1, n.y].isDigit()) {
                    end = i
                    break
                }
            }
            numbers[Vec2i(start, n.y)] = grid[start..end, n.y].toInt()
            found.add(Vec2i(start, n.y))
        }
        return found
    }
    puzzle(3, "Gear Ratios") {
        val grid = CharGrid.parse(lines)
        val numbers = mutableMapOf<Vec2i, Int>()
        grid.forEach { x, y, c ->
            if (c.isDigit() || c == '.') return@forEach
            findNumbers(grid, x, y, numbers)
        }
        numbers.values.sum()
    }
    puzzle(3, "Part Two") {
        val grid = CharGrid.parse(lines)
        val numbers = mutableMapOf<Vec2i, Int>()
        var sumRatios = 0
        grid.forEach { x, y, c ->
            if (c != '*') return@forEach
            val found = findNumbers(grid, x, y, numbers)
            if (found.size != 2) return@forEach
            sumRatios += numbers[found.first()]!! * numbers[found.last()]!!
        }
        sumRatios
    }
}