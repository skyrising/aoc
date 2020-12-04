package de.skyrising.aoc2020

import java.util.regex.Pattern

class BenchmarkDay2 : BenchmarkDay(2)

fun registerDay2() {
    puzzleS(2, "Password Philosophy v1") {
        val pattern = Pattern.compile("^(?<min>\\d+)-(?<max>\\d+) (?<char>.): (?<password>.*)$")
        var valid = 0
        outer@ for (line in it) {
            val match = pattern.matcher(line)
            if (!match.find()) {
                println("Could not parse $line")
                continue
            }
            val min = match.group("min").toInt()
            val max = match.group("max").toInt()
            val c = match.group("char")[0]
            val password = match.group("password").toCharArray()
            var count = 0
            for (pc in password) {
                if (pc == c) {
                    count++
                    if (count > max) continue@outer
                }
            }
            if (count >= min) valid++
        }
        valid
    }
    puzzleB(2, "Password Philosophy v2") {
        var valid = 0
        for (line in it) {
            valid += day2(line) { min, max, c, start, end ->
                var count = 0
                for (i in start until end) {
                    if (line[i] == c) {
                        count++
                        if (count > max) return@day2 false
                    }
                }
                count >= min
            }
        }
        valid
    }
    puzzleS(2, "Part Two v1") {
        val pattern = Pattern.compile("^(?<first>\\d+)-(?<second>\\d+) (?<char>.): (?<password>.*)$")
        var valid = 0
        for (line in it) {
            val match = pattern.matcher(line)
            if (!match.find()) {
                println("Could not parse $line")
                continue
            }
            val first = match.group("first").toInt()
            val second = match.group("second").toInt()
            val c = match.group("char")[0]
            val password = match.group("password").toCharArray()
            if ((password[first - 1] == c) xor (password[second - 1] == c)) {
                valid++
            }
        }
        valid
    }
    puzzleB(2, "Part Two v2") {
        var valid = 0
        for (line in it) {
            valid += day2(line) { first, second, c, start, _ ->
                (line[start + first - 1] == c) xor (line[start + second - 1] == c)
            }
        }
        valid
    }
}