package de.skyrising.aoc2015.day4

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import java.security.MessageDigest

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2015, 4)

@Suppress("unused")
fun register() {
    part1("The Ideal Stocking Stuffer") {
        val input = string.trim()
        var i = 1
        while (true) {
            val hashInput = input + i
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(hashInput.toByteArray())
            if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2].toUByte() < 0x10u) return@part1 i
            i++
        }
    }
    part2 {
        val input = string.trim()
        var i = 1
        while (true) {
            val hashInput = input + i
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(hashInput.toByteArray())
            if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2] == 0.toByte()) return@part2 i
            i++
        }
    }
}