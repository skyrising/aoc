package de.skyrising.aoc2022

import it.unimi.dsi.fastutil.chars.CharOpenHashSet

class BenchmarkDay3 : BenchmarkDayV1(3)

fun registerDay3() {
    puzzle(3, "Rucksack Reorganization") {
        var sum = 0
        for (line in lines) {
            val comp1 = line.slice(0 until line.length / 2)
            val comp2 = line.slice(line.length / 2 until line.length)
            val items1 = CharOpenHashSet(comp1.toCharArray())
            val items2 = CharOpenHashSet(comp2.toCharArray())
            items1.retainAll(items2)
            val same = items1.single()
            sum += when (same) {
                in 'a'..'z' -> same.code - 'a'.code + 1
                in 'A'..'Z' -> same.code - 'A'.code + 27
                else -> 0
            }
        }
        sum
    }

    puzzle(3, "Part Two") {
        var sum = 0
        for (i in lines.indices step 3) {
            val items1 = CharOpenHashSet(lines[i].toCharArray())
            val items2 = CharOpenHashSet(lines[i + 1].toCharArray())
            val items3 = CharOpenHashSet(lines[i + 2].toCharArray())
            items1.retainAll(items2)
            items1.retainAll(items3)
            val same = items1.single()
            sum += when (same) {
                in 'a'..'z' -> same.code - 'a'.code + 1
                in 'A'..'Z' -> same.code - 'A'.code + 27
                else -> 0
            }
        }
        sum
    }
}