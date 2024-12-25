@file:PuzzleName("Point of Incidence")

package de.skyrising.aoc2023.day13

import de.skyrising.aoc.*

val test = TestInput("""
    #.##..##.
    ..#.##.#.
    ##......#
    ##......#
    ..#.##.#.
    ..##..##.
    #.#.##.#.
    
    #...##..#
    #....#..#
    ..##..###
    #####.##.
    #####.##.
    ..##..###
    #....#..#
""")

fun CharGrid.checkVertical(x: Int) = (0..<minOf(x, width - x)).sumOf {
    (0..<height).count { y -> this[x - it - 1, y] != this[x + it, y] }
}

fun CharGrid.checkHorizontal(y: Int) = (0..<minOf(y, height - y)).sumOf {
    (0..<width).count { x -> this[x, y - it - 1] != this[x, y + it] }
}

fun PuzzleInput.part1() = lines.splitOnEmpty().sumOf {
    CharGrid.parse(it).run {
        val xm = (1 until width).firstOrNull { x -> checkVertical(x) == 0 } ?: 0
        val ym = (1 until height).firstOrNull { y -> checkHorizontal(y) == 0 } ?: 0
        xm + ym * 100
    }
}

fun PuzzleInput.part2() = lines.splitOnEmpty().sumOf {
    CharGrid.parse(it).run {
        val xm = (1 until width).firstOrNull { x -> checkVertical(x) == 1 } ?: 0
        val ym = (1 until height).firstOrNull { y -> checkHorizontal(y) == 1 } ?: 0
        xm + ym * 100
    }
}
