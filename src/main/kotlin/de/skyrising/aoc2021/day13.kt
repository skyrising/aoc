package de.skyrising.aoc2021

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap

class BenchmarkDay13 : BenchmarkDayV1(13)

fun registerDay13() {
    val test = listOf(
        "6,10",
        "0,14",
        "9,10",
        "0,3",
        "10,4",
        "4,11",
        "6,0",
        "6,12",
        "4,1",
        "0,13",
        "10,12",
        "3,4",
        "3,0",
        "8,4",
        "1,10",
        "2,14",
        "8,10",
        "9,0",
        "",
        "fold along y=7",
        "fold along x=5"
    )
    val font = Object2CharOpenHashMap<String>()
    font.defaultReturnValue('?')
    font["### #  #### #  ##  #### "] = 'B'
    font["#####   ### #   #   ####"] = 'E'
    font[" ## #  ##   # ###  # ###"] = 'G'
    font["#  ## # ##  # # # # #  #"] = 'K'
    font["  ##   #   #   ##  # ## "] = 'J'
    font["#  ##  ##  ##  ##  # ## "] = 'U'
    puzzleLS(13, "Transparent Origami") {
        val (points, instructions) = readInput(it)
        fold(points, instructions.getInt(0)).size
    }
    puzzleLS(13, "Part Two") {
        val (points, instructions) = readInput(it)
        var folded = points
        for (i in instructions.indices) {
            folded = fold(folded, instructions.getInt(i))
        }
        val max = folded.reduce { acc, pair -> Pair(maxOf(acc.first, pair.first), maxOf(acc.second, pair.second)) }
        val result = StringBuilder((max.first + 2) * (max.second + 1) + 9)
        val chars = Array(8) { CharArray(24) }
        result.append('\n')
        for (y in 0 .. max.second) {
            for (x in 0 .. max.first) {
                val char = if (Pair(x, y) in folded) '#' else ' '
                if (x % 5 < 4) {
                    println("$x,$y ${x / 5},${y * 4 + (x % 5)}")
                    chars[x / 5][y * 4 + (x % 5)] = char
                }
                result.append(char)
            }
            result.append('\n')
        }
        for (char in chars) {
            result.append(font.getChar(String(char)))
        }
        result.toString()
    }
}

private fun readInput(input: List<String>): Pair<Set<Pair<Int, Int>>, IntList> {
    val points = mutableSetOf<Pair<Int, Int>>()
    val instructions = IntArrayList()
    var i = 0
    while (i < input.size) {
        val line = input[i++]
        if (line.isEmpty()) break
        val (x, y) = line.split(',')
        points.add(x.toInt() to y.toInt())
    }
    while (i < input.size) {
        val line = input[i++]
        if (line[11] == 'y') {
            instructions.add(-line.substring(13).toInt())
        } else {
            instructions.add(line.substring(13).toInt())
        }
    }
    return points to instructions
}

private fun fold(points: Set<Pair<Int, Int>>, axis: Int) = if (axis < 0) foldY(points, -axis) else foldX(points, axis)

private fun foldX(points: Set<Pair<Int, Int>>, axis: Int): Set<Pair<Int, Int>> {
    val newPoints = HashSet<Pair<Int, Int>>(points.size)
    for (p in points) {
        if (p.first > axis) {
            newPoints.add(2 * axis - p.first to p.second)
        } else {
            newPoints.add(p)
        }
    }
    return newPoints
}

private fun foldY(points: Set<Pair<Int, Int>>, axis: Int): Set<Pair<Int, Int>> {
    val newPoints = HashSet<Pair<Int, Int>>(points.size)
    for (p in points) {
        if (p.second > axis) {
            newPoints.add(p.first to 2 * axis - p.second)
        } else {
            newPoints.add(p)
        }
    }
    return newPoints
}