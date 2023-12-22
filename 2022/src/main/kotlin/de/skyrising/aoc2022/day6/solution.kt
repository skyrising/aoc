package de.skyrising.aoc2022.day6

import de.skyrising.aoc.*

@PuzzleName("Tuning Trouble")
fun PuzzleInput.part1(): Any? {
    val window = CharArray(4)
    for (i in chars.indices) {
        val c = chars[i]
        window[i % 4] = c
        if (i < 4) continue
        if (window.toSet().size == 4) return i + 1
    }
    return null
}

fun PuzzleInput.part2(): Any? {
    val window = CharArray(14)
    for (i in chars.indices) {
        val c = chars[i]
        window[i % 14] = c
        if (i < 14) continue
        if (window.toSet().size == 14) return i + 1
    }
    return null
}
