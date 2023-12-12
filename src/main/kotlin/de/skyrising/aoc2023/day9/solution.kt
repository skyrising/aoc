package de.skyrising.aoc2023.day9

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntList

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 9)

inline fun generate(list: IntList, pick: IntList.()->Int): IntList {
    var splitPoint = list.size
    while (true) {
        list[splitPoint - 1] = pick(list.subList(0, splitPoint))
        splitPoint--
        var allZero = true
        for (i in 0 until splitPoint) {
            val diff = list.getInt(i + 1) - list.getInt(i)
            list[i] = diff
            if (diff != 0) allZero = false
        }
        if (allZero) return list.subList(splitPoint, list.size)
    }
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """)
    part1("Mirage Maintenance") {
        lines.sumOf {
            generate(it.ints()) { getInt(lastIndex) }.sum()
        }
    }
    part2 {
        lines.sumOf {
            generate(it.ints()) { getInt(0) }.reduce { a, b -> b - a }
        }
    }
}