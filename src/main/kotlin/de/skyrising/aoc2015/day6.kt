package de.skyrising.aoc2015

import java.util.*

class BenchmarkDay6 : BenchmarkDayV1(6)

fun coord(s: String): Pair<Int, Int> {
    val (x, y) = s.split(',')
    return x.toInt() to y.toInt()
}

fun registerDay6() {
    puzzleLS(6, "Probably a Fire Hazard") {
        val lights = BitSet(1_000_000)
        for (line in it) {
            val parts = line.split(' ')
            when (parts[0]) {
                "turn" -> {
                    val on = parts[1] == "on"
                    val from = coord(parts[2])
                    val to = coord(parts[4])
                    for (y in from.second .. to.second) {
                        lights.set(y * 1000 + from.first, y * 1000 + to.first + 1, on)
                    }
                }
                "toggle" -> {
                    val from = coord(parts[1])
                    val to = coord(parts[3])
                    for (y in from.second .. to.second) {
                        lights.flip(y * 1000 + from.first, y * 1000 + to.first + 1)
                    }
                }
            }
        }
        lights.cardinality()
    }
    puzzleLS(6, "Part Two") {
        val lights = ShortArray(1_000_000)
        for (line in it) {
            val parts = line.split(' ')
            when (parts[0]) {
                "turn" -> {
                    val delta = if (parts[1] == "on") 1 else -1
                    val from = coord(parts[2])
                    val to = coord(parts[4])
                    for (y in from.second .. to.second) {
                        for (x in from.first .. to.first) {
                            val l = lights[y * 1000 + x]
                            lights[y * 1000 + x] = maxOf(0, l + delta).toShort()
                        }
                    }
                }
                "toggle" -> {
                    val from = coord(parts[1])
                    val to = coord(parts[3])
                    for (y in from.second .. to.second) {
                        for (x in from.first .. to.first) {
                            lights[y * 1000 + x] = (lights[y * 1000 + x] + 2).toShort()
                        }
                    }
                }
            }
        }
        lights.sum()
    }
}