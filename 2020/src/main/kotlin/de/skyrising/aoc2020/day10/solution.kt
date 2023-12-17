package de.skyrising.aoc2020.day10

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntOpenHashSet

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2020, 10)

@Suppress("unused")
fun register() {
    val test = TestInput("""
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
    """)
    part1("Adapter Array") {
        val numbers = lines.map(String::toInt).sorted()
        val diffs = IntArray(3)
        var prev = 0
        for (n in numbers) {
            diffs[n - prev - 1]++
            prev = n
        }
        diffs[0] * (diffs[2] + 1)
    }
    part2 {
        val numbers = lines.map(String::toInt).sorted()
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
        graph.countPaths(0, numbers.last())
    }
}