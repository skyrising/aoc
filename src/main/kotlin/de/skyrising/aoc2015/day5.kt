package de.skyrising.aoc2015

import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay5 : BenchmarkDayV1(5)

private fun isNice1(s: String): Boolean {
    if (!Regex("[aeiou].*[aeiou].*[aeiou]").containsMatchIn(s)) return false
    var doubleLetter = false
    for (i in 1 .. s.lastIndex) {
        if (s[i] == s[i - 1]) doubleLetter = true
    }
    if (!doubleLetter) return false
    if (s.contains(Regex("ab|cd|pq|xy"))) return false
    return true
}

private fun isNice2(s: String): Boolean {
    var doublePair = false
    for (i in 0 until s.lastIndex) {
        val pair = s.substring(i, i + 2)
        if (s.indexOf(pair, i + 2) != -1) {
            doublePair = true
            break
        }
    }
    if (!doublePair) return false
    for (i in 0 .. s.lastIndex - 2) {
        if (s[i] == s[i + 2]) return true
    }
    return false
}

@Suppress("unused")
fun registerDay5() {
    part1("Doesn't He Have Intern-Elves For This?") {
        lines.count(::isNice1)
    }
    part2 {
        lines.count(::isNice2)
    }
}