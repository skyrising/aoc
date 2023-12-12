package de.skyrising.aoc2020.day3

import de.skyrising.aoc.BenchmarkBase
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBase(2020, 3)

@Suppress("unused")
fun register() {
    part1("Toboggan Trajectory") {
        var trees = 0
        var x = 0
        for (line in lines) {
            val tree = line[x % line.length] == '#'
            if (tree )trees++
            x += 3
        }
        trees
    }
    part1("Toboggan Trajectory") {
        var trees = 0
        var x = 0
        val len = byteLines[0].remaining()
        for (line in byteLines) {
            val tree = line[x] == '#'.code.toByte()
            if (tree) trees++
            x = wrap(x + 3, len)
        }
        trees
    }
    part2 {
        val slopes = intArrayOf(1, 3, 5, 7, 1)
        val step = intArrayOf(1, 1, 1, 1, 2)
        val trees = LongArray(5)
        val x = intArrayOf(0, 0, 0, 0, 0)
        for ((lineCount, line) in lines.withIndex()) {
            for (i in 0..4) {
                if (lineCount % step[i] == 0) {
                    val tree = line[x[i] % line.length] == '#'
                    if (tree) trees[i]++
                    x[i] += slopes[i]
                }
            }
        }
        //trees.contentToString()
        trees.reduce {a, b -> a * b}
    }
    part2 {
        var t0 = 0
        var t1 = 0
        var t2 = 0
        var t3 = 0
        var t4 = 0
        var x0 = 0
        var x1 = 0
        var x2 = 0
        var x3 = 0
        var x4 = 0
        val len = byteLines[0].remaining()
        for ((lineCount, line) in byteLines.withIndex()) {
            t0 += if (line[x0] == '#'.code.toByte()) 1 else 0
            x0 = wrap(x0 + 1, len)
            if (x0 >= len) x0 -= len
            t1 += if (line[x1] == '#'.code.toByte()) 1 else 0
            x1 = wrap(x1 + 3, len)
            if (x1 >= len) x1 -= len
            t2 += if (line[x2] == '#'.code.toByte()) 1 else 0
            x2 = wrap(x2 + 5, len)
            if (x2 >= len) x2 -= len
            t3 += if (line[x3] == '#'.code.toByte()) 1 else 0
            x3 = wrap(x3 + 7, len)
            if (x3 >= len) x3 -= len
            if (lineCount % 2 == 0) {
                t4 += if (line[x4] == '#'.code.toByte()) 1 else 0
                x4 = wrap(x4 + 1, len)
                if (x4 >= len) x4 -= len
            }
        }
        t0.toLong() * t1 * t2 * t3 * t4
    }
}

inline fun wrap(x: Int, len: Int) = x - if (x >= len) len else 0