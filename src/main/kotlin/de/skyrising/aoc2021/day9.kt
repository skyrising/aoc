package de.skyrising.aoc2021

import it.unimi.dsi.fastutil.ints.IntArrayList

class BenchmarkDay9 : BenchmarkDayV1(9)

fun registerDay9() {
    val test = listOf("2199943210",
        "3987894921",
        "9856789892",
        "8767896789",
        "9899965678")
    puzzleLS(9, "Smoke Basin") {
        val (points, width, height) = parseInput(it)
        var risk = 0
        forEachLowPoint(points, width, height) { _, _, value ->
            //println("$x,$y $value")
            risk += value + 1
        }
        risk
    }
    puzzleLS(9, "Part Two") {
        val (points, width, height) = parseInput(it)
        val basins = IntArrayList()
        forEachLowPoint(points, width, height) { x, y, _ ->
            val basin = mutableSetOf<Pair<Int, Int>>()
            searchBasin(points, width, height, basin, x, y)
            basins.add(basin.size)
            //println("$x,$y $v ${basin.size} $basin")
        }
        basins.sort()
        basins.getInt(basins.lastIndex) * basins.getInt(basins.lastIndex - 1) * basins.getInt(basins.lastIndex - 2)
    }
}

fun parseInput(input: List<String>): Triple<Array<IntArray>, Int, Int> {
    val points = Array<IntArray>(input.size) { line -> input[line].chars().map { n -> n - '0'.code }.toArray() }
    val width = points[0].size
    val height = points.size
    return Triple(points, width, height)
}

inline fun forEachLowPoint(map: Array<IntArray>, width: Int, height: Int, fn: (Int, Int, Int) -> Unit) {
    for (y in 0 until height) {
        for (x in 0 until width) {
            val v = map[y][x]
            if (x > 0 && map[y][x - 1] <= v) continue
            if (x < width - 1 && map[y][x + 1] <= v) continue
            if (y > 0 && map[y - 1][x] <= v) continue
            if (y < height - 1 && map[y + 1][x] <= v) continue
            fn(x, y, v)
        }
    }
}

fun searchBasin(map: Array<IntArray>, width: Int, height: Int, basin: MutableSet<Pair<Int, Int>>, x: Int, y: Int) {
    val value = map[y][x]
    if (value == 9 || !basin.add(x to y)) return
    if (x > 0 && map[y][x - 1] >= value) searchBasin(map, width, height, basin, x - 1, y)
    if (x < width - 1 && map[y][x + 1] >= value) searchBasin(map, width, height, basin, x + 1, y)
    if (y > 0 && map[y - 1][x] >= value) searchBasin(map, width, height, basin, x, y - 1)
    if (y < height - 1 && map[y + 1][x] >= value) searchBasin(map, width, height, basin, x, y + 1)
}