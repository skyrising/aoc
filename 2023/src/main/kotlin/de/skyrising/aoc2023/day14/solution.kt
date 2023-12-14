package de.skyrising.aoc2023.day14

import de.skyrising.aoc.*
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

fun CharGrid.cycle() {
    slideNorth()
    slideWest()
    slideSouth()
    slideEast()
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
        var grid = charGrid
        val limit = 1_000_000_000
        val grids = ArrayList<CharGrid>()
        val indexes = Object2IntOpenHashMap<CharGrid>()
        for (i in 0  until limit) {
            indexes[grid] = i
            grids.add(grid)
            grid = grid.subGrid(Vec2i.ZERO, Vec2i(grid.width, grid.height))
            grid.cycle()
            val previous = indexes.getOrDefault(grid as Any, -1)
            if (previous >= 0) {
                val diff = i + 1 - previous
                val remaining = limit - i - 1
                val offset = previous + (remaining % diff)
                return@part2 grids[offset].sumOf { if (it.charValue == 'O') grid.height - it.key.y else 0 }
            }
        }
        error("Expected cycle")
    }
}