package de.skyrising.aoc2022.day20

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2022, 20)

class CircularLongListItem(val value: Long) {
    var next = this
    var prev = this

    fun connect(next: CircularLongListItem) {
        next.prev = this
        this.next = next
    }

    fun move(steps: Int) {
        if (steps == 0) return
        val dest = this + steps
        val p = prev
        val n = next
        next = dest.next
        prev = dest
        prev.next = this
        next.prev = this
        p.next = n
        n.prev = p
    }

    operator fun plus(steps: Int): CircularLongListItem {
        var dest = this
        if (steps > 0) {
            repeat(steps) {
                dest = dest.next
            }
        } else if (steps < 0) {
            repeat(-steps + 1) {
                dest = dest.prev
            }
        }
        return dest
    }
}

private fun parseInput(input: PuzzleInput, multiplier: Long): Pair<List<CircularLongListItem>, CircularLongListItem> {
    val numbers = input.lines.map { CircularLongListItem(it.toLong() * multiplier) }
    var zero: CircularLongListItem? = null
    for (i in numbers.indices) {
        val n = numbers[i]
        if (n.value == 0L) zero = n
        n.connect(numbers[(i + 1) % numbers.size])
    }
    if (zero == null) error("No zero")
    return numbers to zero
}

private fun groveCoordinates(input: PuzzleInput, multiplier: Long, mixes: Int): Long {
    val (numbers, zero) = parseInput(input, multiplier)
    repeat(mixes) {
        for (item in numbers) {
            item.move((item.value % (numbers.size - 1)).toInt())
        }
    }
    val a = zero + 1000
    val b = a + 1000
    val c = b + 1000
    return a.value + b.value + c.value
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        1
        2
        -3
        3
        -2
        0
        4
    """)
    part1("Grove Positioning System") {
        groveCoordinates(this, 1, 1)
    }
    part2 {
        groveCoordinates(this, 811589153, 10)
    }
}