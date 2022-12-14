package de.skyrising.aoc2022

import it.unimi.dsi.fastutil.ints.IntArrayList

class BenchmarkDay1 : BenchmarkDayV1(1)

fun registerDay1() {
    puzzle(1, "Calorie Counting") {
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

    puzzle(1, "Part Two") {
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