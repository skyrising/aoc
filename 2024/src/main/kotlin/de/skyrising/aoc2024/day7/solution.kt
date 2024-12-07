package de.skyrising.aoc2024.day7

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongArrayList

val test = TestInput("""
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
""")


fun canBuild(target: Long, values: List<Long>, fn: (Long, Long) -> Sequence<Long>): Boolean {
    if (values.size == 1) return values[0] == target
    val newValues = LongArrayList(values.size - 1)
    val firstValues = fn(values[0], values[1])
    newValues.add(0)
    newValues.addAll(values.subList(2, values.size))
    for (first in firstValues) {
        newValues[0] = first
        if (canBuild(target, newValues, fn)) return true
    }
    return false
}

@PuzzleName("Bridge Repair")
fun PuzzleInput.part1() = lines.sumOf {
    val (result, inputs) = it.longs().splitOffFirst()
    if (canBuild(result, inputs) { a, b -> sequenceOf(a + b, a * b) }) result else 0
}

fun PuzzleInput.part2() = lines.sumOf {
    val (result, inputs) = it.longs().splitOffFirst()
    if (canBuild(result, inputs) { a, b -> sequence {
        yield(a + b)
        yield(a * b)
        yield("$a$b".toLong())
    } }) result else 0
}
