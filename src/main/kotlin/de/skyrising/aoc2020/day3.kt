package de.skyrising.aoc2020

class BenchmarkDay3 : BenchmarkDay(3)

fun registerDay3() {
    puzzleS(3, "Toboggan Trajectory v1") {
        var trees = 0
        var x = 0
        for (line in it) {
            val tree = line[x % line.length] == '#'
            if (tree )trees++
            x += 3
        }
        trees
    }
    puzzleB(3, "Toboggan Trajectory v2") {
        var trees = 0
        var x = 0
        val len = it[0].remaining()
        for (line in it) {
            val tree = line[x] == '#'.toByte()
            if (tree) trees++
            x = wrap(x + 3, len)
        }
        trees
    }
    puzzleS(3, "Part Two v1") {
        val slopes = intArrayOf(1, 3, 5, 7, 1)
        val step = intArrayOf(1, 1, 1, 1, 2)
        val trees = LongArray(5)
        val x = intArrayOf(0, 0, 0, 0, 0)
        for ((lineCount, line) in it.withIndex()) {
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
    puzzleB(3, "Part Two v2") {
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
        val len = it[0].remaining()
        for ((lineCount, line) in it.withIndex()) {
            t0 += if (line[x0] == '#'.toByte()) 1 else 0
            x0 = wrap(x0 + 1, len)
            if (x0 >= len) x0 -= len
            t1 += if (line[x1] == '#'.toByte()) 1 else 0
            x1 = wrap(x1 + 3, len)
            if (x1 >= len) x1 -= len
            t2 += if (line[x2] == '#'.toByte()) 1 else 0
            x2 = wrap(x2 + 5, len)
            if (x2 >= len) x2 -= len
            t3 += if (line[x3] == '#'.toByte()) 1 else 0
            x3 = wrap(x3 + 7, len)
            if (x3 >= len) x3 -= len
            if (lineCount % 2 == 0) {
                t4 += if (line[x4] == '#'.toByte()) 1 else 0
                x4 = wrap(x4 + 1, len)
                if (x4 >= len) x4 -= len
            }
        }
        t0.toLong() * t1 * t2 * t3 * t4
    }
}