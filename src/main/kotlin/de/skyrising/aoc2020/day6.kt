package de.skyrising.aoc2020

import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.chars.CharOpenHashSet

@Suppress("unused")
class BenchmarkDay6 : BenchmarkDay(6)

@Suppress("unused")
fun registerDay6() {
    part1("Custom Customs") {
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
        sum + group.size
    }
    part1("Custom Customs") {
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
        sum + Integer.bitCount(group)
    }
    part2 {
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
        sum + group.size
    }
    part2 {
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
        sum + Integer.bitCount(group)
    }
}