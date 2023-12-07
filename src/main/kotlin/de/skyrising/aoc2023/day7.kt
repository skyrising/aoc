package de.skyrising.aoc2023

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay7 : BenchmarkDayV1(7)

enum class HandType {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD
}

@Suppress("unused")
fun registerDay7() {
    val test = TestInput("""
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """)
    open class Hand(val cards: String) : Comparable<Hand> {
        open val order get() = "AKQJT98765432"

        open fun type(): HandType {
            val c = cards.histogram()
            return when(c.size) {
                1 -> HandType.FIVE_OF_A_KIND
                2 -> if (c.values.any { it == 3 }) HandType.FULL_HOUSE else HandType.FOUR_OF_A_KIND
                3 -> if (c.values.any { it == 3 }) HandType.THREE_OF_A_KIND else HandType.TWO_PAIR
                4 -> HandType.ONE_PAIR
                else -> HandType.HIGH_CARD
            }
        }

        override fun compareTo(other: Hand): Int {
            val bestKindCmp = type().compareTo(other.type())
            if (bestKindCmp != 0) return -bestKindCmp
            for (i in 0..<5) {
                val cmp = order.indexOf(cards[i]).compareTo(order.indexOf(other.cards[i]))
                if (cmp != 0) return -cmp
            }
            return 0
        }

        override fun toString() = cards
    }

    class Hand2(cards: String) : Hand(cards) {
        override val order get() = "AKQT98765432J"

        override fun type(): HandType {
            val c = cards.histogram()
            val j = c.remove('J')
            if (j == 0) return super.type()
            return when(c.size) {
                0, 1 -> HandType.FIVE_OF_A_KIND
                2 -> {
                    if (j == 1) {
                        if (c.values.all { it == 2 }) return HandType.FULL_HOUSE
                        if (c.values.any { it == 2 }) return HandType.THREE_OF_A_KIND
                    }
                    HandType.FOUR_OF_A_KIND
                }
                3 -> HandType.THREE_OF_A_KIND
                else -> HandType.ONE_PAIR
            }
        }
    }

    fun parse(input: PuzzleInput, part1: Boolean = true) = input.lines.map {
        Pair(if (part1) Hand(it.substring(0, 5)) else Hand2(it.substring(0, 5)), it.substring(6).toInt())
    }
    part1("Camel Cards") {
        parse(this, true).sortedBy { it.first }.withIndex().sumOf { (i, it) -> it.second * (i + 1) }
    }
    part2 {
        parse(this, false).sortedBy { it.first }.withIndex().sumOf { (i, it) -> it.second * (i + 1) }
    }
}