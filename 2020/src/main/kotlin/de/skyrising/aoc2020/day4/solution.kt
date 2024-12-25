@file:PuzzleName("Passport Processing")

package de.skyrising.aoc2020.day4

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName

fun PuzzleInput.part1v0(): Any {
    val fields = mutableSetOf<String>()
    val required = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
    var valid = 0
    for (line in lines) {
        if (line.isEmpty()) {
            if (fields.containsAll(required)) valid++
            fields.clear()
        }
        val parts = line.split(" ")
        for (part in parts) {
            if (part.isEmpty()) continue
            fields.add(part.split(":")[0])
        }
    }
    if (fields.containsAll(required)) valid++
    return valid
}

fun PuzzleInput.part1v1(): Any {
    val fields = mutableSetOf<String>()
    val required = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
    var valid = 0
    for (line in lines) {
        if (line.isEmpty()) {
            if (fields.size == 7) valid++
            fields.clear()
        }
        val parts = line.split(" ")
        for (part in parts) {
            if (part.isEmpty()) continue
            val field = part.split(":")[0]
            if (field in required) fields.add(field)
        }
    }
    if (fields.size == 7) valid++
    return valid
}

fun PuzzleInput.part2v0(): Any {
    val fields = mutableSetOf<String>()
    var valid = 0
    val yearRegex = Regex("^\\d{4}$")
    val hgtRegex = Regex("^(\\d+)(cm|in)$")
    val hclRegex = Regex("^#[0-9a-f]{6}$")
    val ecls = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
    val pidRegex = Regex("^\\d{9}$")
    for (line in lines) {
        if (line.isEmpty()) {
            if (fields.size == 7) valid++
            fields.clear()
        }
        val parts = line.split(" ")
        for (part in parts) {
            if (part.isEmpty()) continue
            val (field, value) = part.split(":")
            val fieldValid = when (field) {
                "byr" -> value.matches(yearRegex) && value.toInt() in 1920..2002
                "iyr" -> value.matches(yearRegex) && value.toInt() in 2010..2020
                "eyr" -> value.matches(yearRegex) && value.toInt() in 2020..2030
                "hgt" -> {
                    val match = hgtRegex.matchEntire(value)
                    if (match != null) {
                        val h = match.groups[1]!!.value.toInt()
                        when (match.groups[2]!!.value) {
                            "cm" -> h in 150..193
                            "in" -> h in 59..76
                            else -> false
                        }
                    } else false
                }
                "hcl" -> value.matches(hclRegex)
                "ecl" -> value in ecls
                "pid" -> value.matches(pidRegex)
                else -> false
            }
            // println("$field ${if (fieldValid) "valid" else "invalid"}: $value")
            if (fieldValid) fields.add(field)
        }
    }
    if (fields.size == 7) valid++
    return valid
}

fun PuzzleInput.part2v1(): Any {
    val fields = mutableSetOf<String>()
    var valid = 0
    val hclRegex = Regex("^#[0-9a-f]{6}$")
    val ecls = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
    val pidRegex = Regex("^\\d{9}$")
    for (line in lines) {
        if (line.isEmpty()) {
            if (fields.size == 7) valid++
            fields.clear()
        }
        val parts = line.split(" ")
        for (part in parts) {
            if (part.isEmpty()) continue
            val (field, value) = part.split(":")
            val fieldValid = try {
                when (field) {
                    "byr" -> value.toInt() in 1920..2002
                    "iyr" -> value.toInt() in 2010..2020
                    "eyr" -> value.toInt() in 2020..2030
                    "hgt" -> {
                        val h = value.substring(0, value.length - 2).toInt()
                        when (value.substring(value.length - 2)) {
                            "cm" -> h in 150..193
                            "in" -> h in 59..76
                            else -> false
                        }
                    }
                    "hcl" -> value.matches(hclRegex)
                    "ecl" -> value in ecls
                    "pid" -> value.matches(pidRegex)
                    else -> false
                }
            } catch (_: NumberFormatException) {
                false
            }
            // println("$field ${if (fieldValid) "valid" else "invalid"}: $value")
            if (fieldValid) fields.add(field)
        }
    }
    if (fields.size == 7) valid++
    return valid
}
