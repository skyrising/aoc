package de.skyrising.aoc2015

import de.skyrising.aoc.Vec2i
import java.util.*

class BenchmarkDay6 : BenchmarkDayV1(6)

fun registerDay6() {
    puzzle(6, "Probably a Fire Hazard") {
        val lights = BitSet(1_000_000)
        for (line in lines) {
            val parts = line.split(' ')
            when (parts[0]) {
                "turn" -> {
                    val on = parts[1] == "on"
                    val from = Vec2i.parse(parts[2])
                    val to = Vec2i.parse(parts[4])
                    for (y in from.y .. to.y) {
                        lights.set(y * 1000 + from.x, y * 1000 + to.x + 1, on)
                    }
                }
                "toggle" -> {
                    val from = Vec2i.parse(parts[1])
                    val to = Vec2i.parse(parts[3])
                    for (y in from.y .. to.y) {
                        lights.flip(y * 1000 + from.x, y * 1000 + to.x + 1)
                    }
                }
            }
        }
        lights.cardinality()
    }
    puzzle(6, "Part Two") {
        val lights = ShortArray(1_000_000)
        for (line in lines) {
            val parts = line.split(' ')
            when (parts[0]) {
                "turn" -> {
                    val delta = if (parts[1] == "on") 1 else -1
                    val from = Vec2i.parse(parts[2])
                    val to = Vec2i.parse(parts[4])
                    for (y in from.y .. to.y) {
                        for (x in from.x .. to.x) {
                            val l = lights[y * 1000 + x]
                            lights[y * 1000 + x] = maxOf(0, l + delta).toShort()
                        }
                    }
                }
                "toggle" -> {
                    val from = Vec2i.parse(parts[1])
                    val to = Vec2i.parse(parts[3])
                    for (y in from.y .. to.y) {
                        for (x in from.x .. to.x) {
                            lights[y * 1000 + x] = (lights[y * 1000 + x] + 2).toShort()
                        }
                    }
                }
            }
        }
        lights.sum()
    }
}