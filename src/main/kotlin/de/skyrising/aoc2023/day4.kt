package de.skyrising.aoc2023

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet

@Suppress("unused")
class BenchmarkDay4 : BenchmarkDayV1(4)

@Suppress("unused")
fun registerDay4() {
    val test = TestInput("""
        Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
        Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
        Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
        Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
        Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
    """)
    data class Card(val index: Int, val winning: IntSet, val have: IntList)
    fun parse(input: PuzzleInput) = input.lines.map { line ->
        val (name, cards) = line.split(": ")
        val (winning, numbers) = cards.split(" | ")
        Card(name.substringAfterLast(' ').toInt(), IntOpenHashSet(winning.ints()), IntArrayList(numbers.ints()))
    }
    part1("") {
        val cards = parse(this)
        cards.sumOf { card ->
            val haveWinning = card.have.filter { it in card.winning }
            if (haveWinning.isEmpty()) 0 else (1 shl (haveWinning.size - 1))
        }
    }
    part2 {
        val cards = parse(this)
        val deck = IntArray(cards.size) { 1 }
        for ((i, count) in deck.withIndex()) {
            if (count == 0) continue
            val card = cards[i]
            val winning = card.have.filter { it in card.winning }.size
            for (j in i+1..i+winning) deck[j] += count
        }
        deck.sum()
    }
}