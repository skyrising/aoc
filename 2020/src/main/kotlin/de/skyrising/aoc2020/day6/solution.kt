@file:PuzzleName("Custom Customs")

package de.skyrising.aoc2020.day6

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import it.unimi.dsi.fastutil.chars.CharOpenHashSet

fun PuzzleInput.part1v0(): Any {
    var sum = 0
    val group = CharOpenHashSet()
    for (line in lines) {
        if (line.isEmpty()) {
            sum += group.size
            group.clear()
        } else {
            for (c in line) group.add(c)
        }
    }
    return sum + group.size
}

fun PuzzleInput.part1v1(): Any {
    var sum = 0
    var group = 0
    for (line in byteLines) {
        val len = line.remaining()
        if (len == 0) {
            sum += Integer.bitCount(group)
            group = 0
        } else {
            for (i in 0 until len) {
                group = group or (1 shl (line[i] - 'a'.code.toByte()))
            }
        }
    }
    return sum + Integer.bitCount(group)
}

fun PuzzleInput.part2v0(): Any {
    var sum = 0
    val group = CharOpenHashSet()
    var first = true
    for (line in lines) {
        if (line.isEmpty()) {
            sum += group.size
            group.clear()
            first = true
        } else if (first) {
            for (c in line) group.add(c)
            first = false
        } else {
            val person = CharOpenHashSet()
            for (c in line) person.add(c)
            group.retainAll(person)
        }
    }
    return sum + group.size
}

fun PuzzleInput.part2v1(): Any {
    var sum = 0
    var group = 0
    var first = true
    for (line in byteLines) {
        val len = line.remaining()
        if (len == 0) {
            sum += Integer.bitCount(group)
            group = 0
            first = true
        } else {
            var person = 0
            for (i in 0 until len) {
                person = person or (1 shl (line[i] - 'a'.code.toByte()))
            }
            if (first) {
                group = person
                first = false
            } else {
                group = group and person
            }
        }
    }
    return sum + Integer.bitCount(group)
}
