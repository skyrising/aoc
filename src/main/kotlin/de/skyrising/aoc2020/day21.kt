package de.skyrising.aoc2020

import java.util.*
import kotlin.collections.HashSet

class BenchmarkDay21 : BenchmarkDayV1(21)

private fun solve(input: List<String>): Pair<List<String>, Map<String, String>> {
    val allIngredients = mutableListOf<String>()
    val map = mutableMapOf<String, MutableSet<String>>()
    for (line in input) {
        val split = line.split('(', limit = 2)
        if (split.size == 1) continue
        val allergens = split[1].substring(9, split[1].length - 1).split(", ")
        val ingredients = split[0].trim().split(' ')
        allIngredients.addAll(ingredients)
        for (allergen in allergens) {
            map[allergen]?.retainAll(ingredients)
            if (allergen !in map) map[allergen] = HashSet(ingredients)
        }
    }
    val found = mutableMapOf<String, String>()
    while (map.isNotEmpty()) {
        val iter = map.iterator()
        while (iter.hasNext()) {
            val (k, v) = iter.next()
            if (v.size == 1) {
                found[k] = v.single()
                iter.remove()
            } else {
                v.removeAll(found.values)
            }
        }
    }
    return Pair(allIngredients, found)
}

fun registerDay21() {
    val test = """
        mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
        trh fvjkl sbzzf mxmxvkd (contains dairy)
        sqjhc fvjkl (contains soy)
        sqjhc mxmxvkd sbzzf (contains fish)
    """.trimIndent().split("\n")
    puzzleLS(21, "Allergen Assessment v1") {
        val (allIngredients, found) = solve(it)
        allIngredients.count { it !in found.values }
    }
    puzzleLS(21, "Part 2 v1") {
        val (_, found) = solve(it)
        val sorted = TreeMap(found)
        sorted.values.joinToString(",") { s -> s }
    }
}