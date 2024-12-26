@file:PuzzleName("Print Queue")

package de.skyrising.aoc2024.day5

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

val test = TestInput("""
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
""")

fun orderComparator(order0: List<IntList>): (Int, Int) -> Int {
    val order = order0.mapTo(LongOpenHashSet()) { PackedIntPair(it.toPair()).longValue }
    return { a, b -> when {
        PackedIntPair(a, b).longValue in order -> -1
        PackedIntPair(b, a).longValue in order -> 1
        else -> 0
    }}
}

typealias Prepared = List<Pair<List<Int>, List<Int>>>

fun PuzzleInput.prepare(): Prepared {
    val (order, updates) = lines.map { it.ints() }.splitOnEmpty(2)
    return updates.map { update ->
        val sorted = update.sortedWith(orderComparator(order))
        update to sorted
    }
}

fun Prepared.part1() = sumOf { (update, sorted) ->
    if (update == sorted) update.middleElement else 0
}

fun Prepared.part2() = sumOf { (update, sorted) ->
    if (update != sorted) sorted.middleElement else 0
}
