package de.skyrising.aoc2023.day13

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 13)

@Suppress("unused")
fun register() {
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
    fun CharGrid.checkVertical(x: Int): Boolean {
        for (offset in 0 until minOf(x, width - x)) {
            for (y in 0 until height) if (this[x - offset - 1, y] != this[x + offset, y]) return false
        }
        return true
    }
    fun CharGrid.checkHorizontal(y: Int): Boolean {
        for (offset in 0 until minOf(y, height - y)) {
            for (x in 0 until width) if (this[x, y - offset - 1] != this[x, y + offset]) return false
        }
        return true
    }
    part1("Point of Incidence") {
        lines.splitOnEmpty().map(CharGrid.Companion::parse).sumOf {
            val xm = (1 until it.width).firstOrNull(it::checkVertical) ?: 0
            val ym = (1 until it.height).firstOrNull(it::checkHorizontal) ?: 0
            xm + ym * 100
        }
    }
    part2 {
        lines.splitOnEmpty().map(CharGrid.Companion::parse).sumOf {
            val xm0 = (1 until it.width).firstOrNull(it::checkVertical) ?: 0
            val ym0 = (1 until it.height).firstOrNull(it::checkHorizontal) ?: 0
            for (sx in 0 until it.width) {
                for (sy in 0 until it.height) {
                    val prev = it[sx, sy]
                    it[sx, sy] = if(prev == '.') '#' else '.'
                    val xm = (1 until it.width).firstOrNull { x ->
                        x != xm0 && it.checkVertical(x)
                    } ?: 0
                    val ym = (1 until it.height).firstOrNull { y ->
                        y != ym0 && it.checkHorizontal(y)
                    } ?: 0
                    if (xm != 0 || ym != 0) return@sumOf xm + ym * 100
                    it[sx, sy] = prev
                }
            }
            0
        }
    }
}