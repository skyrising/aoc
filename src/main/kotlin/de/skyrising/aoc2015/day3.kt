package de.skyrising.aoc2015

class BenchmarkDay3 : BenchmarkDayV1(3)

fun registerDay3() {
    puzzle(3, "Perfectly Spherical Houses in a Vacuum") {
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
    puzzle(3, "Part Two") {
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