package de.skyrising.aoc2022

import de.skyrising.aoc.characters
import de.skyrising.aoc.parseDisplay
import java.lang.StringBuilder
import kotlin.math.abs

class BenchmarkDay10 : BenchmarkDayV1(10)

private inline fun runProgram(instructions: List<String>, cycle: (x: Int) -> Unit) {
    var x = 1
    for (line in instructions) {
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
    puzzleLS(10, "Cathode-Ray Tube") {
        var result = 0
        var cycle = 0
        runProgram(it) { x ->
            if (++cycle % 40 == 20) {
                result += cycle * x
            }
        }
        result
    }
    puzzleLS(10, "Part Two") {
        var cycle = 0
        val output = StringBuilder()
        runProgram(it) { x ->
            output.append(if (abs(x - cycle % 40) <= 1) "█" else " ")
            if (++cycle % 40 == 0) output.append('\n')
        }
        val display = output.delete(output.length - 1, output.length).toString()
        println(display)
        parseDisplay(display)
    }
}