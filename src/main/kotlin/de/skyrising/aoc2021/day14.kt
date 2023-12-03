package de.skyrising.aoc2021

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap
import it.unimi.dsi.fastutil.chars.Char2LongMap
import it.unimi.dsi.fastutil.chars.Char2LongOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2LongMap
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2CharMap
import it.unimi.dsi.fastutil.objects.Object2CharOpenHashMap

@Suppress("unused")
class BenchmarkDay14 : BenchmarkDayV1(14)

@Suppress("unused")
fun registerDay14() {
    val test = TestInput("""
        NNCB
        
        CH -> B
        HH -> N
        CB -> H
        NH -> C
        HB -> C
        HC -> B
        HN -> C
        NN -> C
        BH -> H
        NC -> B
        NB -> B
        BN -> B
        BB -> N
        BC -> B
        CC -> N
        CN -> C
    """)
    part1("Extended Polymerization") {
        val (start, rules) = parseInput(this)
        var current = start
        for (step in 1..10) {
            val sb = StringBuilder(current.length * 2)
            for (i in 0 until current.lastIndex) {
                sb.append(current[i])
                val insert = rules.getChar(current.substring(i, i + 2))
                if (insert != 0.toChar()) {
                    sb.append(insert)
                }
            }
            sb.append(current.last())
            current = sb.toString()
        }
        val counts = Char2IntOpenHashMap(26)
        for (c in current) counts[c]++
        val max = counts.maxOf(Map.Entry<Char, Int>::value)
        val min = counts.minOf(Map.Entry<Char, Int>::value)
        max - min
    }
    part1("Extended Polymerization") {
        val (start, rules) = parseInput(this)
        solveDay14Fast(start, rules, 10)
    }
    part2 {
        val (start, rules) = parseInput(this)
        solveDay14Fast(start, rules, 40)
    }
}

private fun solveDay14Fast(start: String, rules: Object2CharMap<String>, steps: Int): Long {
    val lastChar = start.last()
    var current = Int2LongOpenHashMap(start.length - 1)
    for (i in 0 until start.lastIndex) {
        current[pairIndex(start[i], start[i + 1])]++
    }
    //println("start: ${countCharacters(current, lastChar).toSortedMap()}")
    for (step in 1..steps) {
        val newCurrent = Int2LongOpenHashMap(current.size * 2)
        for (e in current.int2LongEntrySet()) {
            val first = firstOfPair(e.intKey)
            val last = secondOfPair(e.intKey)
            val count = e.longValue
            val middle = rules.getChar(String(charArrayOf(first, last)))
            if (middle != 0.toChar()) {
                newCurrent[pairIndex(first, middle)] += count
                newCurrent[pairIndex(middle, last)] += count
            } else {
                newCurrent[e.intKey] += count
            }
        }
        current = newCurrent
        //println("$step: ${countCharacters(current, lastChar).toSortedMap()}")
    }
    val counts = countCharacters(current, lastChar)
    val max = counts.maxOf(Map.Entry<Char, Long>::value)
    val min = counts.minOf(Map.Entry<Char, Long>::value)
    return max - min
}

private fun pairIndex(a: Char, b: Char) = a.code or (b.code shl 8)
private fun firstOfPair(pair: Int) = (pair and 0xff).toChar()
private fun secondOfPair(pair: Int) = (pair shr 8).toChar()

private fun parseInput(input: PuzzleInput): Pair<String, Object2CharMap<String>> {
    val start = input.lines[0]
    val rules = Object2CharOpenHashMap<String>(input.lines.size - 2)
    for (i in 2 until input.lines.size) {
        val (a, b) = input.lines[i].split(" -> ")
        rules[a] = b[0]
    }
    return start to rules
}

private fun countCharacters(pairCounts: Int2LongMap, lastChar: Char): Char2LongMap {
    val result = Char2LongOpenHashMap()
    for (e in pairCounts.int2LongEntrySet()) {
        result[firstOfPair(e.intKey)] += e.longValue
    }
    result[lastChar]++
    return result
}