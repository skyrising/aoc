@file:PuzzleName("Password Philosophy")

package de.skyrising.aoc2020.day2

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import java.nio.ByteBuffer
import java.util.regex.Pattern

fun PuzzleInput.part1v0(): Any {
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
    return valid
}

fun PuzzleInput.part1v1(): Any {
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
    return valid
}

fun PuzzleInput.part2v0(): Any {
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
    return valid
}

fun PuzzleInput.part2v1(): Any {
    var valid = 0
    for (line in byteLines) {
        valid += day2(line) { first, second, c, start, _ ->
            (line[start + first - 1] == c) xor (line[start + second - 1] == c)
        }
    }
    return valid
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
