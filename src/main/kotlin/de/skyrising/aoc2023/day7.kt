package de.skyrising.aoc2023

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.bytes.Byte2IntMap
import java.nio.ByteBuffer

@Suppress("unused")
class BenchmarkDay7 : BenchmarkDayV1(7)

enum class HandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND,
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

    fun type(histogram: Byte2IntMap) = when(histogram.size) {
        1 -> HandType.FIVE_OF_A_KIND
        2 -> if (3 in histogram.values) HandType.FULL_HOUSE else HandType.FOUR_OF_A_KIND
        3 -> if (3 in histogram.values) HandType.THREE_OF_A_KIND else HandType.TWO_PAIR
        4 -> HandType.ONE_PAIR
        else -> HandType.HIGH_CARD
    }

    val lut = byteArrayOf(6, 15, 4, 13, 2, 0, 9, 0, 0, 7, 14, 5, 0, 3, 12, 8)

    open class Hand(val cards: ByteBuffer) : Comparable<Hand> {
        private val type = type()
        private val cardValues = ByteArray(cards.remaining()) { order(cards[it]) }

        protected open fun order(c: Byte) = when (c.toInt()) {
            'J'.code -> 11
            else -> lut[((c * 9) xor (c.toInt() shr 3)) and 0xf]
        }

        protected open fun type() = type(cards.histogram())

        override fun compareTo(other: Hand): Int {
            val bestKindCmp = type.compareTo(other.type)
            if (bestKindCmp != 0) return bestKindCmp
            for (i in 0..<5) {
                val cmp = cardValues[i].compareTo(other.cardValues[i])
                if (cmp != 0) return cmp
            }
            return 0
        }
    }

    class Hand2(cards: ByteBuffer) : Hand(cards) {
        override fun order(c: Byte) = when (c.toInt()) {
            'J'.code -> 1
            else -> lut[((c * 9) xor (c.toInt() shr 3)) and 0xf]
        }

        override fun type(): HandType {
            val hist = cards.histogram()
            val jokers = hist.remove('J'.code.toByte())
            if (jokers == 5) return HandType.FIVE_OF_A_KIND
            hist[hist.keys.maxBy { hist[it] }] += jokers
            return type(hist)
        }
    }

    fun run(input: PuzzleInput, ctor: (ByteBuffer)->Hand) = input.byteLines.map {
        Pair(ctor(it.slice(0, 5)), it.slice(6, it.remaining() - 6).toInt())
    }.sortedBy { it.first }.withIndex().sumOf { (i, it) -> it.second * (i + 1) }

    part1("Camel Cards") {
        run(this, ::Hand)
    }
    part2 {
        run(this, ::Hand2)
    }
}