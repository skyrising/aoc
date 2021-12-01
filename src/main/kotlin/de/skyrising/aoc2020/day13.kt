package de.skyrising.aoc2020

import de.skyrising.aoc.positionAfter
import de.skyrising.aoc.splitToRanges
import de.skyrising.aoc.until
import it.unimi.dsi.fastutil.longs.LongArrayList

class BenchmarkDay13 : BenchmarkDay(13)

fun modInv(a: Long, b: Long): Long {
    var a = a
    var b = b
    val m = b
    var q = 0L
    var x = 0L
    var y = 1L
    if (b == 1L) return 0

    while (a > 1) {
        if (b == 1L) {
            y = x
            break
        }
        // println("$a / $b")
        q = a / b
        run {
            val t = b
            b = a - q * b
            a = t
        }
        run {
            val t = x
            x = y - q * x
            y = t
        }
    }

    if (y < 0) y += m
    return y
}

fun crt(divisors: LongArray, remainders: LongArray): Long {
    val product = remainders.reduce(Long::times)
    var sum = 0L
    for (i in divisors.indices) {
        val partialProduct = product / remainders[i]
        val inverse = modInv(partialProduct, remainders[i])
        // println("x % ${divisors[i]} = ${remainders[i]}, $partialProduct, $inverse")
        sum += partialProduct * inverse * divisors[i]

    }
    return sum % product
}

inline fun crt(n: Int, divisors: (Int) -> Long, remainders: (Int) -> Long): Long {
    var product = 1L
    for (i in 0 until n) product *= remainders(i)
    var sum = 0L
    for (i in 0 until n) {
        val rem = remainders(i)
        val partialProduct = product / rem
        val inverse = modInv(partialProduct, rem)
        sum += partialProduct * inverse * divisors(i)

    }
    return sum % product
}

fun registerDay13() {
    val test = """
        939
        7,13,x,x,59,x,31,19
        """.trimIndent().split("\n")
    puzzleLS(13, "Shuttle Search v1") {
        val earliest = it[0].toInt()
        val buses = it[1].split(",").stream().filter { it != "x" }.mapToInt(String::toInt).toArray()
        var i = earliest
        while (true) {
            for (bus in buses) {
                if (i % bus == 0) return@puzzleLS (i - earliest) * bus
            }
            i++
        }
    }
    puzzleLS(13, "Shuttle Search v2") {
        val earliest = it[0].toInt()
        val buses = it[1].split(",").stream().filter { it != "x" }.mapToInt(String::toInt).toArray()
        var pair = Pair(0, Integer.MAX_VALUE)
        for (bus in buses) {
            if (earliest % bus == 0) return@puzzleLS 0
            val dist = bus * (earliest / bus + 1) - earliest
            if (dist < pair.second) pair = Pair(bus, dist)
        }
        pair.first * pair.second
    }
    puzzleLS(13, "Part 2 v1") {
        val split = it[1].split(",")
        val buses = LongArrayList()
        val indexes = LongArrayList()
        split.forEachIndexed { i, s ->
            if (s == "x") return@forEachIndexed
            buses.add(s.toLong())
            indexes.add(i.toLong())
        }
        // println(buses)
        // println(indexes)
        crt(
            LongArray(buses.size) { i ->
                val bus = buses.getLong(i)
                (bus - indexes.getLong(i) % bus) % bus
            },
            buses.toLongArray()
        )
    }

    puzzleS(13, "Part 2 v2") {
        val remainders = LongArrayList()
        val divisors = LongArrayList()
        var i = 0
        if (!it.positionAfter('\n')) throw IllegalArgumentException("Invalid input")
        if (!it.until('\n')) throw IllegalArgumentException("Invalid input")
        splitToRanges(it, ',') { from, to ->
            val index = i++
            if (to == from + 1 && this[position() + from] == 'x') return@splitToRanges
            val bus = substring(from, to).toLong()
            remainders.add(bus)
            divisors.add((bus - index % bus) % bus)
        }
        crt(remainders.size, divisors::getLong, remainders::getLong)
    }
}