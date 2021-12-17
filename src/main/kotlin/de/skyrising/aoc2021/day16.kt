package de.skyrising.aoc2021

import de.skyrising.aoc.BitBuf
import java.util.*

class BenchmarkDay16 : BenchmarkDayV1(16)

fun registerDay16() {
    val test = "CE00C43D881120"
    puzzleS(16, "Packet Decoder") {
        val bytes = HexFormat.of().parseHex(it.trimEnd())
        val data = BitBuf(bytes)
        val packets = mutableListOf<Packet>()
        while (true) {
            val packet = readPacket(data) ?: break
            packets.add(packet)
        }
        sumPacketVersions(packets)
    }
    puzzleS(16, "Part Two") {
        val bytes = HexFormat.of().parseHex(it.trimEnd())
        val data = BitBuf(bytes)
        readPacket(data)?.value()
    }
}

private fun readPacket(data: BitBuf): Packet? {
    if (data.readAvailable() < 11) return null
    val version = data.readBits(3)
    val type = data.readBits(3)
    when (type) {
        4 -> {
            var literal = 0L
            var c: Boolean
            do {
                val literalBits = data.readBits(5)
                c = (literalBits shr 4) != 0
                literal = (literal shl 4) or (literalBits and 0xf).toLong()
            } while (c)
            return LiteralPacket(version, type, literal)
        }
        else -> {
            val lengthType = data.readBits(1)
            val packets = mutableListOf<Packet>()
            if (lengthType == 1) {
                val count = data.readBits(11)
                for (i in 0 until count) packets.add(readPacket(data)!!)
            } else {
                val length = data.readBits(15)
                val start = data.readerIndex
                while (data.readerIndex < start + length) {
                    packets.add(readPacket(data)!!)
                }
            }
            return OperatorPacket(version, type, packets)
        }
    }
}

private fun sumPacketVersions(packets: List<Packet>): Int {
    var sum = 0
    for (packet in packets) {
        sum += packet.version
        if (packet is OperatorPacket) {
            sum += sumPacketVersions(packet.subPackets)
        }
    }
    return sum
}

abstract class Packet(val version: Int, val type: Int) {
    override fun toString() = "Packet($version, $type)"

    abstract fun value(): Long
}
class LiteralPacket(version: Int, type: Int, val literal: Long) : Packet(version, type) {
    override fun toString() = "LiteralPacket($version, $literal)"
    override fun value() = literal
}
class OperatorPacket(version: Int, type: Int, val subPackets: List<Packet>) : Packet(version, type) {
    override fun toString(): String {
        return "OperatorPacket($version, $type)$subPackets"
    }
    override fun value(): Long {
        val values = subPackets.map(Packet::value)
        return when (type) {
            0 -> values.reduce { a, b -> a + b }
            1 -> values.reduce { a, b -> a * b }
            2 -> values.minOf { it }
            3 -> values.maxOf { it }
            5 -> if (values[0] > values[1]) 1 else 0
            6 -> if (values[0] < values[1]) 1 else 0
            7 -> if (values[0] == values[1]) 1 else 0
            else -> throw IllegalStateException("Unknown type $type")
        }
    }
}