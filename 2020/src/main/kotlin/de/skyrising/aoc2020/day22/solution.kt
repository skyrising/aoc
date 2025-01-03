@file:PuzzleName("Crab Combat")

package de.skyrising.aoc2020.day22

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableSet
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.collections.set
import kotlin.collections.withIndex

private fun readInput(input: PuzzleInput): Map<Int, LinkedList<Int>> {
    val map = mutableMapOf<Int, LinkedList<Int>>()
    var current = LinkedList<Int>()
    var id = -1
    for (line in input.lines) {
        when {
            line.startsWith("Player ") -> {
                id = line.substring(7, line.length - 1).toInt()
            }
            line.isNotEmpty() -> {
                current.add(line.toInt())
            }
            else -> {
                map[id] = current
                current = LinkedList<Int>()
                id = -1
            }
        }
    }
    if (id != -1) map[id] = current
    return map
}

private fun round(a: LinkedList<Int>, b: LinkedList<Int>): Boolean {
    if (a.isEmpty() || b.isEmpty()) return true
    val a0 = a.poll()
    val b0 = b.poll()
    when {
        a0 > b0 -> {
            a.add(a0)
            a.add(b0)
        }
        a0 < b0 -> {
            b.add(b0)
            b.add(a0)
        }
        else -> {
            a.add(a0)
            b.add(b0)
        }
    }
    return a.isEmpty() || b.isEmpty()
}

private fun gameRecursive(p1: LinkedList<Int>, p2: LinkedList<Int>): Int {
    val previous = mutableSetOf<Pair<List<Int>, List<Int>>>()
    while (true) {
        val result = roundRecursive(p1, p2, previous)
        if (result != 0) return result
    }
}

private fun roundRecursive(p1: LinkedList<Int>, p2: LinkedList<Int>, previous: MutableSet<Pair<List<Int>, List<Int>>>): Int {
    if (previous.contains(Pair(p1, p2))) return 1
    previous.add(Pair(ArrayList(p1), ArrayList(p2)))
    val a = p1.poll()
    val b = p2.poll()
    val winner = if (a > p1.size || b > p2.size) {
        if (a > b) 1 else 2
    } else {
        gameRecursive(LinkedList(p1.subList(0, a)), LinkedList(p2.subList(0, b)))
    }
    if (winner == 1) {
        p1.add(a)
        p1.add(b)
        if (p2.isEmpty()) return 1
    } else if (winner == 2) {
        p2.add(b)
        p2.add(a)
        if (p1.isEmpty()) return 2
    }
    return 0
}

val test = TestInput("""
    Player 1:
    9
    2
    6
    3
    1

    Player 2:
    5
    8
    4
    7
    10
""")

val test2 = TestInput("""
    Player 1:
    43
    19

    Player 2:
    2
    29
    14
""")

fun PuzzleInput.part1(): Any {
    val (p1, p2) = ArrayList(readInput(this).values)
    while (!round(p1, p2)) {}
    var sum = 0
    val winner = if (p1.isEmpty()) p2 else p1
    for ((i, j) in winner.withIndex()) {
        sum += (winner.size - i) * j
    }
    return sum
}

fun PuzzleInput.part2(): Any {
    val (p1, p2) = ArrayList(readInput(this).values)
    gameRecursive(p1, p2)
    var sum = 0
    val winner = if (p1.isEmpty()) p2 else p1
    for ((i, j) in winner.withIndex()) {
        sum += (winner.size - i) * j
    }
    return sum
}
