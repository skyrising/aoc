package de.skyrising.aoc2022

import it.unimi.dsi.fastutil.chars.CharOpenHashSet

class BenchmarkDay3 : BenchmarkDayV1(3)

fun registerDay3() {
    puzzleLS(3, "Rucksack Reorganization") {
        var sum = 0
        for (line in it) {
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

    puzzleLS(3, "Part Two") {
        var sum = 0
        for (i in it.indices step 3) {
            val items1 = CharOpenHashSet(it[i].toCharArray())
            val items2 = CharOpenHashSet(it[i + 1].toCharArray())
            val items3 = CharOpenHashSet(it[i + 2].toCharArray())
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