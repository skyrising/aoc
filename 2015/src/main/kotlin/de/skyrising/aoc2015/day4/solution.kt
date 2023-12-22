package de.skyrising.aoc2015.day4

import de.skyrising.aoc.*
import java.security.MessageDigest

@PuzzleName("The Ideal Stocking Stuffer")
fun PuzzleInput.part1(): Any {
    val input = string.trim()
    var i = 1
    while (true) {
        val hashInput = input + i
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(hashInput.toByteArray())
        if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2].toUByte() < 0x10u) return i
        i++
    }
}

fun PuzzleInput.part2(): Any {
    val input = string.trim()
    var i = 1
    while (true) {
        val hashInput = input + i
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(hashInput.toByteArray())
        if (bytes[0] == 0.toByte() && bytes[1] == 0.toByte() && bytes[2] == 0.toByte()) return i
        i++
    }
}