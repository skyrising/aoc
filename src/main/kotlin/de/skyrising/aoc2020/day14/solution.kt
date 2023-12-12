package de.skyrising.aoc2020.day14

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
import java.nio.ByteBuffer

@Suppress("unused")
class BenchmarkDay : BenchmarkBase(2020, 14)

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
        if (char == 'X'.code.toByte()) {
            actualMask = actualMask or (1L shl pos)
            floating.add(pos)
        } else if (char == '1'.code.toByte()) {
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
        val digit = buf[pos + i] - '0'.code.toByte()
        l = 10 * l + digit
    }
    return l
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
        mem[8] = 11
        mem[7] = 101
        mem[8] = 0
    """)
    val test2 = TestInput("""
        mask = 000000000000000000000000000000X1001X
        mem[42] = 100
        mask = 00000000000000000000000000000000X0XX
        mem[26] = 1
    """)
    part1("Docking Data") {
        val mem = Long2LongOpenHashMap()
        var mask = -1L
        var maskBits = 0L
        for (line in lines) {
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
    part1("Docking Data") {
        val mem = Long2LongOpenHashMap()
        var mask = -1L
        var maskBits = 0L
        for (line in byteLines) {
            if (line[1] == 'a'.code.toByte()) {
                val m = getMask(line)
                mask = m.first
                maskBits = m.second
            } else {
                val buf = line.slice()
                buf.positionAfter('['.code.toByte())
                buf.until(']'.code.toByte())
                val addr = parseLong(buf)
                buf.clear()
                buf.positionAfter('='.code.toByte())
                buf.position(buf.position() + 1)
                val value = parseLong(buf)
                // println("$addr, $value -> ${(value and mask) or maskBits}")
                mem[addr] = (value and mask) or maskBits
            }
        }
        // println(mem)
        mem.values.sum()
    }
    part2 {
        val mem = Long2LongOpenHashMap()
        var mask = 0L
        var maskBits = 0L
        var floatingPos = IntArray(0)
        for (line in lines) {
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
    part2 {
        val mem = Long2LongOpenHashMap()
        var mask = 0L
        var maskBits = 0L
        var floatingPos = IntArray(0)
        for (line in byteLines) {
            if (line[1] == 'a'.code.toByte()) {
                val m = getMask(line)
                mask = m.first
                maskBits = m.second
                floatingPos = m.third
            } else {
                val buf = line.slice()
                buf.positionAfter('['.code.toByte())
                buf.until(']'.code.toByte())
                val addr = parseLong(buf)
                buf.clear()
                buf.positionAfter('='.code.toByte())
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