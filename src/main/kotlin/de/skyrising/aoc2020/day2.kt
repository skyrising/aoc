package de.skyrising.aoc2020

import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import java.nio.ByteBuffer
import java.util.regex.Pattern

@Suppress("unused")
class BenchmarkDay2 : BenchmarkDay(2)

@Suppress("unused")
fun registerDay2() {
    part1("Password Philosophy") {
        val pattern = Pattern.compile("^(?<min>\\d+)-(?<max>\\d+) (?<char>.): (?<password>.*)$")
        var valid = 0
        outer@ for (line in lines) {
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
    part1("Password Philosophy") {
        var valid = 0
        for (line in byteLines) {
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
    part2 {
        val pattern = Pattern.compile("^(?<first>\\d+)-(?<second>\\d+) (?<char>.): (?<password>.*)$")
        var valid = 0
        for (line in lines) {
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
    part2 {
        var valid = 0
        for (line in byteLines) {
            valid += day2(line) { first, second, c, start, _ ->
                (line[start + first - 1] == c) xor (line[start + second - 1] == c)
            }
        }
        valid
    }
}

private inline fun day2(line: ByteBuffer, predicate: (n1: Int, n2: Int, c: Byte, start: Int, end: Int) -> Boolean): Int {
    val len = line.remaining()
    var num1 = 0
    var num2 = 0
    var i = 0
    while (i < len) {
        val c = line[i++]
        if (c == '-'.code.toByte()) break
        num1 *= 10
        num1 += c - '0'.code.toByte()
    }
    while (i < len) {
        val c = line[i++]
        if (c == ' '.code.toByte()) break
        num2 *= 10
        num2 += c - '0'.code.toByte()
    }
    val c = line[i]
    return if (predicate.invoke(num1, num2, c, i + 3, len)) 1 else 0
}