@file:PuzzleName("Cathode-Ray Tube")

package de.skyrising.aoc2022.day10

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.parseDisplay
import kotlin.math.abs

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

fun PuzzleInput.part1(): Any {
    var result = 0
    var cycle = 0
    runProgram(this) { x ->
        if (++cycle % 40 == 20) {
            result += cycle * x
        }
    }
    return result
}

fun PuzzleInput.part2(): Any {
    var cycle = 0
    val output = StringBuilder()
    runProgram(this) { x ->
        output.append(if (abs(x - cycle % 40) <= 1) "█" else " ")
        if (++cycle % 40 == 0) output.append('\n')
    }
    val display = output.delete(output.length - 1, output.length).toString()
    log(display)
    return parseDisplay(display)
}
