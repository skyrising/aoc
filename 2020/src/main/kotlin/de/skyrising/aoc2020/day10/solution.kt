@file:PuzzleName("Adapter Array")

package de.skyrising.aoc2020.day10

import de.skyrising.aoc.Graph
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.ints.IntOpenHashSet

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

fun PuzzleInput.part1(): Any {
    val numbers = lines.map(String::toInt).sorted()
    val diffs = IntArray(3)
    var prev = 0
    for (n in numbers) {
        diffs[n - prev - 1]++
        prev = n
    }
    return diffs[0] * (diffs[2] + 1)
}

fun PuzzleInput.part2(): Any {
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
    return graph.countPaths(0, numbers.last())
}
