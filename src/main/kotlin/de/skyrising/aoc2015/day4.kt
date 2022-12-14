package de.skyrising.aoc2015

import java.security.MessageDigest

class BenchmarkDay4 : BenchmarkDayV1(4)

fun registerDay4() {
    puzzle(4, "The Ideal Stocking Stuffer") {
        val input = string.trim()
        var i = 1
        while (true) {
            val hashInput = input + i
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(hashInput.toByteArray())
            if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2].toUByte() < 0x10u) return@puzzle i
            i++
        }
    }
    puzzle(4, "Part Two") {
        val input = string.trim()
        var i = 1
        while (true) {
            val hashInput = input + i
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(hashInput.toByteArray())
            if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2] == 0.toByte()) return@puzzle i
            i++
        }
    }
}