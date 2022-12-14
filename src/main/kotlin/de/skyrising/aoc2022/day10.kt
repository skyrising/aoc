package de.skyrising.aoc2022

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.parseDisplay
import kotlin.math.abs

class BenchmarkDay10 : BenchmarkDayV1(10)

private inline fun runProgram(input: PuzzleInput, cycle: (x: Int) -> Unit) {
    var x = 1
    for (line in input.lines) {
        val parts = line.split(" ")
        when (parts[0]) {
            "noop" -> cycle(x)
            "addx" -> {
                cycle(x)
                cycle(x)
                x += parts[1].toInt()
            }
            else -> error("Unknown instruction ${parts[0]}")
        }
    }
}

fun registerDay10() {
    puzzle(10, "Cathode-Ray Tube") {
        var result = 0
        var cycle = 0
        runProgram(this) { x ->
            if (++cycle % 40 == 20) {
                result += cycle * x
            }
        }
        result
    }
    puzzle(10, "Part Two") {
        var cycle = 0
        val output = StringBuilder()
        runProgram(this) { x ->
            output.append(if (abs(x - cycle % 40) <= 1) "█" else " ")
            if (++cycle % 40 == 0) output.append('\n')
        }
        val display = output.delete(output.length - 1, output.length).toString()
        log(display)
        parseDisplay(display)
    }
}