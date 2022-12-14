package de.skyrising.aoc2022

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.TestInput
import java.util.*

class BenchmarkDay8 : BenchmarkDayV1(8)

private fun parseInput(input: PuzzleInput): Array<IntArray> {
    val rows = mutableListOf<IntArray>()
    for (line in input.byteLines) {
        val row = IntArray(line.remaining())
        for (i in 0 until line.remaining()) {
            row[i] = line[i] - '0'.code.toByte()
        }
        rows.add(row)
    }
    return rows.toTypedArray()
}

private fun viewingDistance(limit: Int, lookup: (k: Int) -> Int): Int {
    val self = lookup(limit)
    var highest = 0
    var count = 0
    for (k in limit - 1 downTo 0) {
        val other = lookup(k)
        ++count
        if (other >= self) return count
        highest = maxOf(highest, other)
    }
    return count
}

fun registerDay8() {
    val test = TestInput("""
        30373
        25512
        65332
        33549
        35390
    """)
    puzzle(8, "Treetop Tree House") {
        val rows = parseInput(this)
        val width = rows[0].size
        val height = rows.size
        val visible = BitSet(width * height)
        val down = 0 until height
        val right = 0 until width
        for (i in down) {
            var highest = -1
            for (j in right) {
                if (rows[j][i] > highest) visible.set(j * height + i)
                highest = maxOf(rows[j][i], highest)
            }
            highest = -1
            for (j in right.reversed()) {
                if (rows[j][i] > highest) visible.set(j * height + i)
                highest = maxOf(rows[j][i], highest)
            }
        }
        for (j in right) {
            var highest = -1
            for (i in down) {
                if (rows[j][i] > highest) visible.set(j * height + i)
                highest = maxOf(rows[j][i], highest)
            }
            highest = -1
            for (i in down.reversed()) {
                if (rows[j][i] > highest) visible.set(j * height + i)
                highest = maxOf(rows[j][i], highest)
            }
        }
        /*for (j in 0 until height) {
            for (i in 0 until width) {
                if (visible.get(j * width + i)) print(rows[j][i]) else print(' ')
            }
            println()
        }*/
        visible.cardinality()
    }
    puzzle(8, "Part Two") {
        val rows = parseInput(this)
        val width = rows[0].size
        val height = rows.size
        var highscore = 0
        for (j in 1 until height - 1) {
            for (i in 1 until width - 1) {
                val left = viewingDistance(i) { k -> rows[j][k]}
                val right = viewingDistance(width - i - 1) { k -> rows[j][width - k - 1]}
                val up = viewingDistance(j) { k -> rows[k][i]}
                val down = viewingDistance(height - j - 1) { k -> rows[height - k - 1][i]}
                val score = left * right * up * down
                highscore = maxOf(highscore, score)
            }
        }
        highscore
    }
}