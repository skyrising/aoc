package de.skyrising.aoc2022.day1

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.ints.IntArrayList

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 1)

@Suppress("unused")
fun register() {
    part1("Calorie Counting") {
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
        most
    }

    part2 {
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
        elves.getInt(0) + elves.getInt(1) + elves.getInt(2)
    }
}