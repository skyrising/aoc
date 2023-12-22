package de.skyrising.aoc2022.day1

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList

@PuzzleName("Calorie Counting")
fun PuzzleInput.part1(): Any {
    var most = 0
    var current = 0
    for (line in lines) {
        if (line.isBlank()) {
            if (current > most) most = current
            current = 0
            continue
        }
        current += line.toInt()
    }
    return most
}

fun PuzzleInput.part2(): Any {
    val elves = IntArrayList()
    var current = 0
    for (line in lines) {
        if (line.isBlank()) {
            elves.add(current)
            current = 0
            continue
        }
        current += line.toInt()
    }
    elves.sortDescending()
    return elves.getInt(0) + elves.getInt(1) + elves.getInt(2)
}
