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

fun PuzzleInput.sumPossible(predicate: (Long, List<Long>) -> Boolean) = lines.sumOf {
    val (result, inputs) = it.longs().splitOffFirst()
    if (predicate(result, inputs)) result else 0
}

@PuzzleName("Bridge Repair")
fun PuzzleInput.part1() = sumPossible { result, inputs ->
    canBuild(result, inputs) { a, b -> sequenceOf(a + b, a * b) }
}

fun PuzzleInput.part2() = sumPossible { result, inputs ->
    canBuild(result, inputs) { a, b -> sequence {
        yield(a + b)
        yield(a * b)
        yield("$a$b".toLong())
    } }
}

interface NextStep
data class Solved(val possible: Boolean) : NextStep
data class NewTarget(val value: Long) : NextStep

fun testAdd(target: Long, nums: List<Long>): NextStep {
    val n = target - nums[0]
    if (nums.size == 1) return Solved(n == 0L)
    return NewTarget(n)
}

fun testMul(target: Long, nums: List<Long>): NextStep {
    if (target % nums[0] != 0L) return Solved(false)
    val n = target / nums[0]
    if (nums.size == 1) return Solved(n == 1L)
    return NewTarget(n)
}

fun testConcat(target: Long, nums: List<Long>): NextStep {
    val t = target.toString()
    val o = nums[0].toString()
    if (nums.size < 2 || !t.endsWith(o)) return Solved(false)
    val ns = t.substring(0, t.length - o.length)
    if (ns.isEmpty() || ns == "-") return Solved(false)
    val n = ns.toLong()
    if (nums.size == 2) return Solved(n == nums[1])
    return NewTarget(n)
}

fun canBuildReverse(target: Long, values: List<Long>, nextFuns: List<(Long, List<Long>) -> NextStep>): Boolean {
    for (fn in nextFuns) {
        when (val next = fn(target, values)) {
            is Solved -> if (next.possible) return true
            is NewTarget -> if (canBuildReverse(next.value, values.subList(1, values.size), nextFuns)) return true
        }
    }
    return false
}

@PuzzleName("Bridge Repair")
fun PuzzleInput.part1v1() = sumPossible { result, inputs ->
    canBuildReverse(result, inputs.reversed(), listOf(::testAdd, ::testMul))
}

fun PuzzleInput.part2v1() = sumPossible { result, inputs ->
    canBuildReverse(result, inputs.reversed(), listOf(::testAdd, ::testMul, ::testConcat))
}
