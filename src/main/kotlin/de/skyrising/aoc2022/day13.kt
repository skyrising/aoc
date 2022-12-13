package de.skyrising.aoc2022

import kotlinx.serialization.json.*

class BenchmarkDay13 : BenchmarkDayV1(13)

private fun compare(left: JsonElement, right: JsonElement): Int {
    if (left is JsonPrimitive && right is JsonPrimitive) {
        return left.int - right.int
    }
    if (left is JsonArray && right is JsonArray) {
        for (i in left.indices) {
            if (i >= right.size) return 1
            val cmp = compare(left[i], right[i])
            if (cmp != 0) return cmp
        }
        if (left.size != right.size) return left.size - right.size
        return 0
    }
    if (left is JsonPrimitive) {
        return compare(JsonArray(listOf(left)), right)
    }
    if (right is JsonPrimitive) {
        return compare(left, JsonArray(listOf(right)))
    }
    return 0
}

fun registerDay13() {
    puzzleLS(13, "Distress Signal") {
        val values = it.filter(String::isNotBlank).map(Json::parseToJsonElement).chunked(2)
        var result = 0
        for (i in values.indices) {
            val (left, right) = values[i]
            val cmp = compare(left, right)
            if (cmp < 0) result += i + 1
        }
        result
    }
    puzzleLS(13, "Part Two") {
        val values = it.filter(String::isNotBlank).mapTo(mutableListOf(),  Json::parseToJsonElement)
        val div1 = JsonArray(listOf(JsonArray(listOf(JsonPrimitive(2)))))
        val div2 = JsonArray(listOf(JsonArray(listOf(JsonPrimitive(6)))))
        values.add(div1)
        values.add(div2)
        val sorted = values.sortedWith(::compare)
        val div1Index = sorted.indexOf(div1) + 1
        val div2Index = sorted.indexOf(div2) + 1
        div1Index * div2Index
    }
}