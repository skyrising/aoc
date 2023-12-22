package de.skyrising.aoc2020.day21

import de.skyrising.aoc.*
import java.util.*

private fun solve(input: PuzzleInput): Pair<List<String>, Map<String, String>> {
    val allIngredients = mutableListOf<String>()
    val map = mutableMapOf<String, MutableSet<String>>()
    for (line in input.lines) {
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

val test = TestInput("""
    mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
    trh fvjkl sbzzf mxmxvkd (contains dairy)
    sqjhc fvjkl (contains soy)
    sqjhc mxmxvkd sbzzf (contains fish)
""")

@PuzzleName("Allergen Assessment")
fun PuzzleInput.part1(): Any {
    val (allIngredients, found) = solve(this)
    return allIngredients.count { it !in found.values }
}

fun PuzzleInput.part2(): Any {
    val (_, found) = solve(this)
    val sorted = TreeMap(found)
    return sorted.values.joinToString(",") { s -> s }
}
