package de.skyrising.aoc2015

class BenchmarkDay7 : BenchmarkDayV1(7)

fun registerDay7() {
    fun parse(input: List<String>): MutableMap<String, List<String>> {
        val map = mutableMapOf<String, List<String>>()
        for (line in input) {
            val parts = line.split(' ')
            val name = parts[parts.lastIndex]
            map[name] = parts.subList(0, parts.size - 2)
        }
        return map
    }

    fun eval(map: MutableMap<String, List<String>>, name: String): UShort {
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

    puzzleLS(7, "Some Assembly Required") {
        val test = listOf("123 -> x", "456 -> y", "x AND y -> d", "x OR y -> e", "x LSHIFT 2 -> f", "y RSHIFT 2 -> g", "NOT x -> h", "NOT y -> i")
        val map = parse(it)
        eval(map, "a")
    }
    puzzleLS(7, "Part Two") {
        val map = parse(it)
        val map2 = HashMap(map)
        map2["b"] = listOf(eval(map, "a").toString())
        eval(map2, "a")
    }
}