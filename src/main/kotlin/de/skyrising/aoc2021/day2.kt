package de.skyrising.aoc2021

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay2 : BenchmarkDayV1(2)

@Suppress("unused")
fun registerDay2() {
    val test = TestInput("""
        forward 5
        down 5
        forward 8
        up 3
        down 8
        forward 2
    """)
    part1("Dive!") {
        var horizontal = 0
        var depth = 0
        for (line in lines) {
            val (cmd, num) = line.split(' ', limit = 2)
            when (cmd) {
                "forward" -> horizontal += num.toInt()
                "down" -> depth += num.toInt()
                "up" -> depth -= num.toInt()
            }
        }
        depth * horizontal
    }
    part2 {
        var horizontal = 0
        var depth = 0
        var aim = 0
        for (line in lines) {
            val (cmd, num) = line.split(' ', limit = 2)
            when (cmd) {
                "forward" -> {
                    horizontal += num.toInt()
                    depth += num.toInt() * aim
                }
                "down" -> aim += num.toInt()
                "up" -> aim -= num.toInt()
            }
        }
        depth * horizontal
    }
}