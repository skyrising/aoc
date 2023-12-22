package de.skyrising.aoc2021.day13

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

val test = TestInput("""
    6,10
    0,14
    9,10
    0,3
    10,4
    4,11
    6,0
    6,12
    4,1
    0,13
    10,12
    3,4
    3,0
    8,4
    1,10
    2,14
    8,10
    9,0
    
    fold along y=7
    fold along x=5
""")

@PuzzleName("Transparent Origami")
fun PuzzleInput.part1(): Any {
    val (points, instructions) = readInput(this)
    return fold(points, instructions.getInt(0)).size
}

fun PuzzleInput.part2(): Any {
    val (points, instructions) = readInput(this)
    var folded = points
    for (i in instructions.indices) {
        folded = fold(folded, instructions.getInt(i))
    }
    val max = folded.reduce { acc, pair -> Pair(maxOf(acc.first, pair.first), maxOf(acc.second, pair.second)) }
    val result = StringBuilder((max.first + 2) * (max.second + 1) + 9)
    val chars = Array(8) { CharArray(24) }
    result.append('\n')
    for (y in 0 .. max.second) {
        if (y > 0) result.append('\n')
        for (x in 0 .. max.first) {
            val char = if (Pair(x, y) in folded) '█' else ' '
            if (x % 5 < 4) {
                chars[x / 5][y * 4 + (x % 5)] = char
            }
            result.append(char)
        }
    }
    log(result)
    return parseDisplay(result.toString())
}

private fun readInput(input: PuzzleInput): Pair<Set<Pair<Int, Int>>, IntList> {
    val points = mutableSetOf<Pair<Int, Int>>()
    val instructions = IntArrayList()
    var i = 0
    while (i < input.lines.size) {
        val line = input.lines[i++]
        if (line.isEmpty()) break
        val (x, y) = line.split(',')
        points.add(x.toInt() to y.toInt())
    }
    while (i < input.lines.size) {
        val line = input.lines[i++]
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