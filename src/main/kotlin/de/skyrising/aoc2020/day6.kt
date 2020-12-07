package de.skyrising.aoc2020

import it.unimi.dsi.fastutil.chars.CharOpenHashSet

class BenchmarkDay6 : BenchmarkDay(6)

fun registerDay6() {
    puzzleLS(6, "Custom Customs v1") {
        var sum = 0
        val group = CharOpenHashSet()
        for (line in it) {
            if (line.isEmpty()) {
                sum += group.size
                group.clear()
            } else {
                for (c in line) group.add(c)
            }
        }
        sum + group.size
    }
    puzzleLB(6, "Custom Customs v2") {
        var sum = 0
        var group = 0
        for (line in it) {
            val len = line.remaining()
            if (len == 0) {
                sum += Integer.bitCount(group)
                group = 0
            } else {
                for (i in 0 until len) {
                    group = group or (1 shl (line[i] - 'a'.toByte()))
                }
            }
        }
        sum + Integer.bitCount(group)
    }
    puzzleLS(6, "Part 2 v1") {
        var sum = 0
        val group = CharOpenHashSet()
        var first = true
        for (line in it) {
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
    puzzleLB(6, "Part 2 v2") {
        var sum = 0
        var group = 0
        var first = true
        for (line in it) {
            val len = line.remaining()
            if (len == 0) {
                sum += Integer.bitCount(group)
                group = 0
                first = true
            } else {
                var person = 0
                for (i in 0 until len) {
                    person = person or (1 shl (line[i] - 'a'.toByte()))
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