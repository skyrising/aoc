package de.skyrising.aoc2023.day14

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 14)

inline fun CharGrid.shift(i: Int, range: IntProgression, rocks: Int, sett: CharGrid.(Int,Int,Char)->Unit) {
    var r = rocks
    val dist = (if (range.step > 0) range.last - range.first else range.first - range.last) + 1
    if (rocks > 0 && dist > 0 && rocks != dist) {
        for (j1 in range) {
            sett(i, j1, if (r-- > 0) 'O' else '.')
        }
    }
}

inline fun CharGrid.slide(iterI: IntProgression, iterJ: IntProgression, gett: CharGrid.(Int, Int)->Char, crossinline sett: CharGrid.(Int, Int, Char)->Unit) {
    for (i in iterI) {
        var start = iterJ.first
        var rocks = 0
        for (j in iterJ) {
            when(gett(i, j)) {
                '#' -> {
                    shift(i, IntProgression.fromClosedRange(start, j - iterJ.step, iterJ.step), rocks, sett)
                    rocks = 0
                    start = j + iterJ.step
                }
                'O' -> rocks++
            }
        }
        shift(i, IntProgression.fromClosedRange(start, iterJ.last, iterJ.step), rocks, sett)
    }
}

fun CharGrid.slideNorth() {
    slide(0 until width, 0 until height, CharGrid::get, CharGrid::set)
}

fun CharGrid.slideWest() {
    slide(0 until height, 0 until width, CharGrid::getT, CharGrid::setT)
}

fun CharGrid.slideSouth() {
    slide(0 until width, (0 until height).reversed(), CharGrid::get, CharGrid::set)
}

fun CharGrid.slideEast() {
    slide(0 until height, (0 until width).reversed(), CharGrid::getT, CharGrid::setT)
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """)
    val test2 = TestInput("""
        .#.
        .O.
        ...
    """)
    part1("Parabolic Reflector Dish") {
        val grid = charGrid
        grid.slideNorth()
        grid.sumOf { if (it.charValue == 'O') grid.height - it.key.y else 0 }
    }
    part2 {
        val grid = charGrid
        val limit = 1_000_000_000
        val grids = IntArrayList()
        val indexes = Object2IntOpenHashMap<String>()
        indexes.defaultReturnValue(-1)
        indexes[String(grid.data)] = 0
        for (i in 1  .. limit) {
            var sum = 0
            grid.forEach { _, y, c -> if (c == 'O') sum += grid.height - y }
            grids.add(sum)
            grid.slideNorth()
            grid.slideWest()
            grid.slideSouth()
            grid.slideEast()

            val previous = indexes.putIfAbsent(String(grid.data), i)
            if (previous >= 0) {
                val diff = i - previous
                val remaining = limit - i
                val offset = previous + (remaining % diff)
                return@part2 grids.getInt(offset)
            }
        }
        error("Expected cycle")
    }
}