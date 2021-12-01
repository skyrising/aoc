package de.skyrising.aoc2020

import de.skyrising.aoc.Graph
import it.unimi.dsi.fastutil.ints.IntOpenHashSet

class BenchmarkDay10 : BenchmarkDayV1(10)

fun registerDay10() {
    val test = """
        28
        33
        18
        42
        31
        14
        46
        20
        48
        47
        24
        23
        49
        45
        19
        38
        39
        11
        1
        32
        25
        35
        8
        17
        7
        9
        4
        2
        34
        10
        3
        """.trimIndent().split("\n")
    puzzleLS(10, "Adapter Array v1") {
        val numbers = it.map(String::toInt).sorted()
        val diffs = IntArray(3)
        var prev = 0
        for (n in numbers) {
            diffs[n - prev - 1]++
            prev = n
        }
        diffs[0] * (diffs[2] + 1)
    }
    puzzleLS(10, "Part 2 v1") {
        val numbers = it.map(String::toInt).sorted()
        val graph = Graph.build<Int, Nothing?> {
            val numbersSet = IntOpenHashSet(numbers)
            numbersSet.add(0)
            for (n in numbers) {
                for (i in n - 3 until n) {
                    if (i in numbersSet) {
                        edge(i, n, n - i)
                    }
                }
            }
        }
        graph.countPaths(graph[0]!!, graph[numbers[numbers.lastIndex]]!!)
    }
}