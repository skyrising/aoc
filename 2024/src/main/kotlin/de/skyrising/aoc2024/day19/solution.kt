@file:PuzzleName("Linen Layout")

package de.skyrising.aoc2024.day19

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.splitOnEmpty
import it.unimi.dsi.fastutil.objects.Object2LongMap
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap

val test = TestInput("""
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
""")

fun make(target: String, patterns: List<String>, memo: Object2LongMap<String> = Object2LongOpenHashMap()): Long {
    if (target.isEmpty()) return 1
    if (memo.containsKey(target)) return memo.getLong(target)
    var count = 0L
    for (pattern in patterns) {
        if (target.startsWith(pattern)) {
            count += make(target.substring(pattern.length), patterns, memo)
        }
    }
    memo.put(target, count)
    return count
}

fun PuzzleInput.part1(): Any {
    val (patternsS, designs) = lines.splitOnEmpty(2)
    val patterns = patternsS.single().split(", ")
    return designs.count { make(it, patterns) > 0 }
}

fun PuzzleInput.part2(): Any {
    val (patternsS, designs) = lines.splitOnEmpty(2)
    val patterns = patternsS.single().split(", ")
    return designs.sumOf { make(it, patterns) }
}
