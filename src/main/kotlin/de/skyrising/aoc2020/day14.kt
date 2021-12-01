package de.skyrising.aoc2020

import de.skyrising.aoc.positionAfter
import de.skyrising.aoc.until
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
import java.nio.ByteBuffer

class BenchmarkDay14 : BenchmarkDay(14)

private fun getMask(line: String): Pair<Long, Long> {
    val actualMask = line.replace('1', '0').replace('X', '1').toLong(2)
    val maskBits = line.replace('X', '0').toLong(2)
    return Pair(actualMask, maskBits)
}

private fun getMask(line: ByteBuffer): Triple<Long, Long, IntArray> {
    val prefixBytes = 7
    val bits = 36
    var actualMask = 0L
    var maskBits = 0L
    val floating = IntArrayList()
    for (i in 0 until bits) {
        val char = line[prefixBytes + i]
        val pos = bits - i - 1
        if (char == 'X'.toByte()) {
            actualMask = actualMask or (1L shl pos)
            floating.add(pos)
        } else if (char == '1'.toByte()) {
            maskBits = maskBits or (1L shl pos)
        }
    }
    return Triple(actualMask, maskBits, floating.toIntArray())
}

private fun getOneBits(num: Long): IntArray {
    val count = num.countOneBits()
    val bitPos = IntArray(count)
    var n = num
    for (i in 0 until count) {
        val lz = n.countLeadingZeroBits()
        bitPos[i] = 63 - lz
        n = n and (Long.MIN_VALUE ushr lz).inv()
    }
    return bitPos
}

private fun parseLong(buf: ByteBuffer): Long {
    var l = 0L
    val pos = buf.position()
    for (i in 0 until buf.remaining()) {
        val digit = buf[pos + i] - '0'.toByte()
        l = 10 * l + digit
    }
    return l
}

fun registerDay14() {
    val test = """
        mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
        mem[8] = 11
        mem[7] = 101
        mem[8] = 0
        """.trimIndent().split("\n")
    val test2 = """
        mask = 000000000000000000000000000000X1001X
        mem[42] = 100
        mask = 00000000000000000000000000000000X0XX
        mem[26] = 1
    """.trimIndent().split("\n")
    puzzleLS(14, "Docking Data v1") {
        val mem = Long2LongOpenHashMap()
        var mask = -1L
        var maskBits = 0L
        for (line in it) {
            if (line.startsWith("mask = ")) {
                val m = getMask(line.substring(7))
                mask = m.first
                maskBits = m.second
            } else {
                val addr = line.substring(line.indexOf('[') + 1, line.indexOf(']')).toLong()
                val value = line.substring(line.indexOf('=') + 2).toLong()
                // println("$addr, $value -> ${(value and mask) or maskBits}")
                mem[addr] = (value and mask) or maskBits
            }
        }
        // println(mem)
        mem.values.sum()
    }
    puzzleLB(14, "Docking Data v2") {
        val mem = Long2LongOpenHashMap()
        var mask = -1L
        var maskBits = 0L
        for (line in it) {
            if (line[1] == 'a'.toByte()) {
                val m = getMask(line)
                mask = m.first
                maskBits = m.second
            } else {
                val buf = line.slice()
                buf.positionAfter('['.toByte())
                buf.until(']'.toByte())
                val addr = parseLong(buf)
                buf.clear()
                buf.positionAfter('='.toByte())
                buf.position(buf.position() + 1)
                val value = parseLong(buf)
                // println("$addr, $value -> ${(value and mask) or maskBits}")
                mem[addr] = (value and mask) or maskBits
            }
        }
        // println(mem)
        mem.values.sum()
    }
    puzzleLS(14, "Part 2 v1") {
        val mem = Long2LongOpenHashMap()
        var mask = 0L
        var maskBits = 0L
        var floatingPos = IntArray(0)
        for (line in it) {
            if (line.startsWith("mask = ")) {
                val m = getMask(line.substring(7))
                mask = m.first
                maskBits = m.second
                floatingPos = getOneBits(mask)
            } else {
                val addr = line.substring(line.indexOf('[') + 1, line.indexOf(']')).toLong()
                val value = line.substring(line.indexOf('=') + 2).toLong()
                val fixed = (addr or maskBits) and mask.inv()
                for (i in 0 until (1L shl floatingPos.size)) {
                    var addr2 = fixed
                    for (j in floatingPos.indices) {
                        val bit = (i shr j) and 1L
                        val pos = floatingPos[j]
                        addr2 = addr2 or (bit shl pos)
                        // println("$bit, $pos, $addr -> $addr2")
                    }
                    // println("$addr -> $addr2")
                    mem[addr2] = value
                }
            }
        }
        // println(mem)
        mem.values.sum()
    }
    puzzleLB(14, "Part 2 v2") {
        val mem = Long2LongOpenHashMap()
        var mask = 0L
        var maskBits = 0L
        var floatingPos = IntArray(0)
        for (line in it) {
            if (line[1] == 'a'.toByte()) {
                val m = getMask(line)
                mask = m.first
                maskBits = m.second
                floatingPos = m.third
            } else {
                val buf = line.slice()
                buf.positionAfter('['.toByte())
                buf.until(']'.toByte())
                val addr = parseLong(buf)
                buf.clear()
                buf.positionAfter('='.toByte())
                buf.position(buf.position() + 1)
                val value = parseLong(buf)
                val fixed = (addr or maskBits) and mask.inv()
                for (i in 0 until (1L shl floatingPos.size)) {
                    var addr2 = fixed
                    for (j in floatingPos.indices) {
                        val bit = (i shr j) and 1L
                        val pos = floatingPos[j]
                        addr2 = addr2 or (bit shl pos)
                        // println("$bit, $pos, $addr -> $addr2")
                    }
                    // println("$addr -> $addr2")
                    mem[addr2] = value
                }
            }
        }
        // println(mem)
        mem.values.sum()
    }
}