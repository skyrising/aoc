package de.skyrising.aoc2020

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongArrayList

@Suppress("unused")
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

@Suppress("unused")
fun registerDay13() {
    val test = TestInput("""
        939
        7,13,x,x,59,x,31,19
    """)
    part1("Shuttle Search") {
        val earliest = lines[0].toInt()
        val buses = lines[1].split(",").stream().filter { it != "x" }.mapToInt(String::toInt).toArray()
        var i = earliest
        while (true) {
            for (bus in buses) {
                if (i % bus == 0) return@part1 (i - earliest) * bus
            }
            i++
        }
    }
    part1("Shuttle Search") {
        val earliest = lines[0].toInt()
        val buses = lines[1].split(",").stream().filter { it != "x" }.mapToInt(String::toInt).toArray()
        var pair = Pair(0, Integer.MAX_VALUE)
        for (bus in buses) {
            if (earliest % bus == 0) return@part1 0
            val dist = bus * (earliest / bus + 1) - earliest
            if (dist < pair.second) pair = Pair(bus, dist)
        }
        pair.first * pair.second
    }
    part2 {
        val split = lines[1].split(",")
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

    part2 {
        val remainders = LongArrayList()
        val divisors = LongArrayList()
        var i = 0
        if (!chars.positionAfter('\n')) throw IllegalArgumentException("Invalid input")
        if (!chars.until('\n')) throw IllegalArgumentException("Invalid input")
        splitToRanges(chars, ',') { from, to ->
            val index = i++
            if (to == from + 1 && this[position() + from] == 'x') return@splitToRanges
            val bus = substring(from, to).toLong()
            remainders.add(bus)
            divisors.add((bus - index % bus) % bus)
        }
        crt(remainders.size, divisors::getLong, remainders::getLong)
    }
}