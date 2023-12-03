package de.skyrising.aoc2020

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1

@Suppress("unused")
class BenchmarkDay25 : BenchmarkDayV1(25)

private fun findLoop(pk: Long): Int {
    var a = 1L
    var i = 0
    while (a != pk) {
        i++
        a = loopStep(a)
    }
    return i
}

private fun loopStep(a: Long, s: Long = 7) = (a * s) % 20201227

@Suppress("unused")
fun registerDay25() {
    val test = TestInput("""
        5764801
        17807724
    """)
    part1("Combo Breaker") {
        val (pk1, pk2) = lines.map(String::toLong)
        val loop1 = findLoop(pk1)
        val loop2 = findLoop(pk2)
        println("$loop1, $loop2")
        var e = 1L
        repeat(loop1) {
            e = loopStep(e, pk2)
        }
        e
    }
}
