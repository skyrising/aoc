package de.skyrising.aoc2015.day3

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2015, 3)

@Suppress("unused")
fun register() {
    part1("Perfectly Spherical Houses in a Vacuum") {
        val houses = mutableSetOf<Pair<Int, Int>>()
        var x = 0
        var y = 0
        houses.add(x to y)
        for (c in chars) {
            when (c) {
                '>' -> x++
                '^' -> y--
                '<' -> x--
                'v' -> y++
            }
            houses.add(x to y)
        }
        houses.size
    }
    part2 {
        val houses = mutableSetOf<Pair<Int, Int>>()
        val x = arrayOf(0, 0)
        val y = arrayOf(0, 0)
        houses.add(x[0] to y[0])
        var i = 0
        for (c in chars) {
            when (c) {
                '>' -> x[i % 2]++
                '^' -> y[i % 2]--
                '<' -> x[i % 2]--
                'v' -> y[i % 2]++
            }
            houses.add(x[i % 2] to y[i % 2])
            i++
        }
        houses.size
    }
}