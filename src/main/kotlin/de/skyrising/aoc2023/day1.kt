package de.skyrising.aoc2023

import de.skyrising.aoc.TestInput

class BenchmarkDay1 : BenchmarkDayV1(1)

fun digitToInt(s: String) = when (s) {
    "0", "zero" -> 0
    "1", "one" -> 1
    "2", "two" -> 2
    "3", "three" -> 3
    "4", "four" -> 4
    "5", "five" -> 5
    "6", "six" -> 6
    "7", "seven" -> 7
    "8", "eight" -> 8
    "9", "nine" -> 9
    else -> error("Invalid digit: $s")
}

fun registerDay1() {
    val test = TestInput(
        """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent()
    )
    val test2 = TestInput(
        """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent()
    )
    puzzle(1, "Trebuchet?!") {
        lines.sumOf {
            val digits = it.filter(Char::isDigit)
            digits[0].digitToInt() * 10 + digits.last().digitToInt()
        }
    }
    val regexStart = Regex("\\d|one|two|three|four|five|six|seven|eight|nine")
    val regexEnd = Regex(".*(\\d|one|two|three|four|five|six|seven|eight|nine)")
    puzzle(1, "Part Two") {
        lines.sumOf {
            val first = regexStart.find(it)?.value ?: error("No first digit")
            val last = regexEnd.find(it)?.groups?.get(1)?.value ?: error("No last digit")
            val result = digitToInt(first) * 10 + digitToInt(last)
            // val digits = regexStart.findAll(it).map(MatchResult::value).toList()
            // val result2 = digitToInt(digits[0]) * 10 + digitToInt(digits.last())
            // if (result != result2) error("Mismatch: $result != $result2 $it $digits")
            result
        }
    }
}