package de.skyrising.aoc2022

import kotlin.math.abs

class BenchmarkDay10 : BenchmarkDayV1(10)

val test = """
    noop
    addx 3
    addx -5
""".trimIndent().split('\n').filter(String::isNotBlank)

val test1 = """
    addx 15
    addx -11
    addx 6
    addx -3
    addx 5
    addx -1
    addx -8
    addx 13
    addx 4
    noop
    addx -1
    addx 5
    addx -1
    addx 5
    addx -1
    addx 5
    addx -1
    addx 5
    addx -1
    addx -35
    addx 1
    addx 24
    addx -19
    addx 1
    addx 16
    addx -11
    noop
    noop
    addx 21
    addx -15
    noop
    noop
    addx -3
    addx 9
    addx 1
    addx -3
    addx 8
    addx 1
    addx 5
    noop
    noop
    noop
    noop
    noop
    addx -36
    noop
    addx 1
    addx 7
    noop
    noop
    noop
    addx 2
    addx 6
    noop
    noop
    noop
    noop
    noop
    addx 1
    noop
    noop
    addx 7
    addx 1
    noop
    addx -13
    addx 13
    addx 7
    noop
    addx 1
    addx -33
    noop
    noop
    noop
    addx 2
    noop
    noop
    noop
    addx 8
    noop
    addx -1
    addx 2
    addx 1
    noop
    addx 17
    addx -9
    addx 1
    addx 1
    addx -3
    addx 11
    noop
    noop
    addx 1
    noop
    addx 1
    noop
    noop
    addx -13
    addx -19
    addx 1
    addx 3
    addx 26
    addx -30
    addx 12
    addx -1
    addx 3
    addx 1
    noop
    noop
    noop
    addx -9
    addx 18
    addx 1
    addx 2
    noop
    noop
    addx 9
    noop
    noop
    noop
    addx -1
    addx 2
    addx -37
    addx 1
    addx 3
    noop
    addx 15
    addx -21
    addx 22
    addx -6
    addx 1
    noop
    addx 2
    addx 1
    noop
    addx -10
    noop
    noop
    addx 20
    addx 1
    addx 2
    addx 2
    addx -6
    addx -11
    noop
    noop
    noop
""".trimIndent().split('\n').filter(String::isNotBlank)

fun registerDay10() {
    puzzleLS(10, "Cathode-Ray Tube") {
        var result = 0
        var cycle = 0
        var x = 1
        for (line in it) {
            val parts = line.split(" ")
            var add = 0
            val cycles = when (parts[0]) {
                "noop" -> 1
                "addx" -> {
                    add = parts[1].toInt()
                    2
                }
                else -> error("Unknown instruction ${parts[0]}")
            }
            repeat(cycles) {
                cycle++
                if (cycle % 40 == 20) {
                    println("$cycle: $x")
                    result += cycle * x
                }
            }
            x += add
        }
        result
    }
    puzzleLS(10, "Part Two") {
        var cycle = 0
        var x = 1
        fun pixel() {
            // println("Cycle: ${cycle + 1} X: $x CRT: ${cycle % 40} diff: ${x - cycle % 40}")
            val visible = abs(x - cycle % 40) <= 1
            print(if (visible) "#" else ".")
            cycle++
            if (cycle > 0 && cycle % 40 == 0) println()
        }
        for (line in it) {
            val parts = line.split(" ")
            when (parts[0]) {
                "noop" -> pixel()
                "addx" -> {
                    pixel()
                    pixel()
                    x += parts[1].toInt()
                }
                else -> error("Unknown instruction ${parts[0]}")
            }
        }
    }
}