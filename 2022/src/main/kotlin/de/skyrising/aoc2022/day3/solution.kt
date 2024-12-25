@file:PuzzleName("Rucksack Reorganization")

package de.skyrising.aoc2022.day3

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import it.unimi.dsi.fastutil.chars.CharOpenHashSet

fun PuzzleInput.part1(): Any {
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
    return sum
}

fun PuzzleInput.part2(): Any {
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
    return sum
}
