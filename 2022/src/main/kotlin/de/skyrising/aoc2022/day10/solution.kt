package de.skyrising.aoc2022.day10

import de.skyrising.aoc.*
import kotlin.math.abs

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 10)

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

@Suppress("unused")
fun register() {
    part1("Cathode-Ray Tube") {
        var result = 0
        var cycle = 0
        runProgram(this) { x ->
            if (++cycle % 40 == 20) {
                result += cycle * x
            }
        }
        result
    }
    part2 {
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