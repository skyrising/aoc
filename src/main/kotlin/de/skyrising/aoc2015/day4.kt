package de.skyrising.aoc2015

import java.security.MessageDigest

class BenchmarkDay4 : BenchmarkDayV1(4)

fun registerDay4() {
    puzzleS(4, "The Ideal Stocking Stuffer") {
        val input = it.toString().trim()
        var i = 1
        while (true) {
            val hashInput = input + i
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(hashInput.toByteArray())
            if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2].toUByte() < 0x10u) return@puzzleS i
            i++
        }
    }
    puzzleS(4, "Part Two") {
        val input = it.toString().trim()
        var i = 1
        while (true) {
            val hashInput = input + i
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(hashInput.toByteArray())
            if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2] == 0.toByte()) return@puzzleS i
            i++
        }
    }
}