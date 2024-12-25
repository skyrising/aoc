@file:PuzzleName("Some Assembly Required")

package de.skyrising.aoc2015.day7

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.lastIndex
import kotlin.collections.listOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set

private fun parse(input: PuzzleInput): MutableMap<String, List<String>> {
    val map = mutableMapOf<String, List<String>>()
    for (line in input.lines) {
        val parts = line.split(' ')
        val name = parts[parts.lastIndex]
        map[name] = parts.subList(0, parts.size - 2)
    }
    return map
}

private fun eval(map: MutableMap<String, List<String>>, name: String): UShort {
    val parts = map[name] ?: return name.toUShort()
    if (parts.size == 1) {
        return try {
            parts[0].toUShort()
        } catch (e: NumberFormatException) {
            eval(map, parts[0]).also { map[name] = listOf(it.toString()) }
        }
    }
    if (parts[0] == "NOT") {
        return eval(map, parts[1]).inv().also { map[name] = listOf(it.toString()) }
    }
    val x = parts[0]
    val y = parts[2]
    return when(parts[1]) {
        "AND" -> (eval(map, x) and eval(map, y)).also { map[name] = listOf(it.toString()) }
        "OR" -> (eval(map, x) or eval(map, y)).also { map[name] = listOf(it.toString()) }
        "LSHIFT" -> (eval(map, x).toInt() shl eval(map, y).toInt()).toUShort().also { map[name] = listOf(it.toString()) }
        "RSHIFT" -> (eval(map, x).toInt() shr eval(map, y).toInt()).toUShort().also { map[name] = listOf(it.toString()) }
        else -> throw IllegalArgumentException(parts[1])
    }
}

val test = TestInput("""
    123 -> x
    456 -> y
    x AND y -> d
    x OR y -> e
    x LSHIFT 2 -> f
    y RSHIFT 2 -> g
    NOT x -> h
    NOT y -> i
""")

fun PuzzleInput.part1() = eval(parse(this), "a")

fun PuzzleInput.part2(): Any {
    val map = parse(this)
    val map2 = HashMap(map)
    map2["b"] = listOf(eval(map, "a").toString())
    return eval(map2, "a")
}
