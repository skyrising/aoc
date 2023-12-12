package de.skyrising.aoc2020.day11

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2020, 11)

private inline fun scanQueenMove(row: Int, column: Int, grid: List<CharArray>, width: Int, fn: (Char, Int, Int) -> Boolean) {
    for (i in 1 .. minOf(row, column)) if(fn(grid[row - i][column - i], row - i, column - i)) break // NW
    for (i in 1 .. row) if(fn(grid[row - i][column], row - i, column)) break // N
    for (i in 1 .. minOf(row, width - column - 1)) if(fn(grid[row - i][column + i], row - i, column + i)) break // NE
    for (i in 1 until width - column) if(fn(grid[row][column + i], row, column + i)) break // E
    for (i in 1 .. minOf(grid.size - row - 1, width - column - 1)) if(fn(grid[row + i][column + i], row + i, column + i)) break // SE
    for (i in 1 until grid.size - row) if(fn(grid[row + i][column], row + i, column)) break // S
    for (i in 1 .. minOf(grid.size - row - 1, column)) if(fn(grid[row + i][column - i], row + i, column - i)) break // SW
    for (i in 1 .. column) if(fn(grid[row][column - i], row, column - i)) break // W
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        L.LL.LL.LL
        LLLLLLL.LL
        L.L.L..L..
        LLLL.LL.LL
        L.LL.LL.LL
        L.LLLLL.LL
        ..L.L.....
        LLLLLLLLLL
        L.LLLLLL.L
        L.LLLLL.LL
    """)
    val test2 = TestInput("""
        .......#.
        ...#.....
        .#.......
        .........
        ..#L....#
        ....#....
        .........
        #........
        ...#.....
    """)
    part1("Seating System") {
        var grid = mutableListOf<CharArray>()
        for (line in lines) {
            grid.add(line.toCharArray())
        }
        while (true) {
            var newGrid = ArrayList<CharArray>(grid.size)
            var stabilized = true
            for (row in 0 until grid.size) {
                val rowChars = grid[row]
                val newRow = rowChars.copyOf()
                newGrid.add(newRow)
                for (column in rowChars.indices) {
                    val seat = rowChars[column]
                    if (seat == '.') continue
                    var adj = 0
                    for (row2 in row - 1 .. row + 1) {
                        if (row2 < 0 || row2 >= grid.size) continue
                        val rowChars2 = grid[row2]
                        for (column2 in column - 1 .. column + 1) {
                            if (column2 < 0 || column2 >= rowChars2.size) continue
                            if (row2 == row && column2 == column) continue
                            if (rowChars2[column2] == '#') adj++
                        }
                    }
                    when (seat) {
                        'L' -> {
                            if (adj == 0) {
                                newRow[column] = '#'
                                stabilized = false
                            }
                        }
                        '#' -> {
                            if (adj >= 4) {
                                newRow[column] = 'L'
                                stabilized = false
                            }
                        }
                    }
                }
            }

            grid = newGrid

            if (stabilized) {
                var count = 0
                for (row in grid) {
                    for (seat in row) {
                        if (seat == '#') count++
                    }
                }
                return@part1 count
            }
        }
    }
    part2 {
        var grid = mutableListOf<CharArray>()
        var width = 0
        for (line in lines) {
            val row = line.toCharArray()
            assert(width == 0 || width == row.size)
            width = row.size
            grid.add(row)
        }
        while (true) {
            var newGrid = ArrayList<CharArray>(grid.size)
            var stabilized = true
            for (row in 0 until grid.size) {
                val rowChars = grid[row]
                val newRow = rowChars.copyOf()
                newGrid.add(newRow)
                for (column in rowChars.indices) {
                    val seat = rowChars[column]
                    if (seat == '.') continue
                    var adj = 0
                    scanQueenMove(row, column, grid, width) { s, r, c ->
                        // if (seat == 'L') {
                        //     println("$r, $c: $s")
                        // }
                        if (s == '#') adj++
                        s != '.'
                    }
                    //if (seat == 'L') println(adj)
                    when (seat) {
                        'L' -> {
                            if (adj == 0) {
                                newRow[column] = '#'
                                stabilized = false
                            }
                        }
                        '#' -> {
                            if (adj >= 5) {
                                newRow[column] = 'L'
                                stabilized = false
                            }
                        }
                    }
                }
            }

            grid = newGrid

            // for (row in grid) {
            //     println(row)
            // }
            // println()
            if (stabilized) {
                var count = 0
                for (row in grid) {
                    for (seat in row) {
                        if (seat == '#') count++
                    }
                }
                return@part2 count
            }
        }
    }
}