package de.skyrising.aoc2023.day7

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.bytes.Byte2IntMap
import java.nio.ByteBuffer

enum class HandType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND,
}

const val MIRROR_CHAR_CODE = 'Z'.code + 'A'.code

private inline fun packCodes(code: (Int)->Long) = (code(0) shl 32) or (code(1) shl 24) or (code(2) shl 16) or (code(3) shl 8) or code(4)

private fun type(histogram: Byte2IntMap) = when(histogram.size) {
    1 -> HandType.FIVE_OF_A_KIND
    2 -> if (3 in histogram.values) HandType.FULL_HOUSE else HandType.FOUR_OF_A_KIND
    3 -> if (3 in histogram.values) HandType.THREE_OF_A_KIND else HandType.TWO_PAIR
    4 -> HandType.ONE_PAIR
    else -> HandType.HIGH_CARD
}

private fun type2(histogram: Byte2IntMap): HandType {
    val jokers = histogram.remove('J'.code.toByte())
    if (jokers == 5) return HandType.FIVE_OF_A_KIND
    val e = histogram.byte2IntEntrySet().maxBy { it.intValue }
    e.setValue(e.intValue + jokers)
    return type(histogram)
}

private class Hand(val type: HandType, val cardValues: Long, val bid: Int) : Comparable<Hand> {

    override fun compareTo(other: Hand): Int {
        val bestKindCmp = type.compareTo(other.type)
        if (bestKindCmp != 0) return bestKindCmp
        return cardValues.compareTo(other.cardValues)
    }

    companion object {
        private fun order(c: Byte, jCode: Byte) = when (c.toInt()) {
            'J'.code -> jCode
            in '0'.code..'9'.code -> c
            else -> (MIRROR_CHAR_CODE - c).toByte()
        }

        fun part1(cards: ByteBuffer, bid: Int) = Hand(
            type(cards.histogram()),
            packCodes { order(cards[it], (MIRROR_CHAR_CODE - 'R'.code).toByte()).toLong() },
            bid
        )

        fun part2(cards: ByteBuffer, bid: Int) = Hand(
            type2(cards.histogram()),
            packCodes { order(cards[it], '1'.code.toByte()).toLong() },
            bid
        )
    }
}

private inline fun run(input: PuzzleInput, ctor: (ByteBuffer,Int)-> Hand) = input.byteLines.map {
    ctor(it.slice(0, 5), it.slice(6, it.remaining() - 6).toInt())
}.sorted().sumOfWithIndex { i, it -> it.bid * (i + 1) }

val test = TestInput("""
    32T3K 765
    T55J5 684
    KK677 28
    KTJJT 220
    QQQJA 483
""")

@PuzzleName("Camel Cards")
fun PuzzleInput.part1() = run(this, Hand.Companion::part1)
fun PuzzleInput.part2() = run(this, Hand.Companion::part2)
