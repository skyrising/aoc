package de.skyrising.aoc2023.day15

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.indices
import kotlin.collections.linkedMapOf
import kotlin.collections.set
import kotlin.collections.sumOf

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 15)

fun hash(s: String): Int {
    var result = 0
    for (c in s) result = (result + c.code) * 17
    return result and 0xff
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
    """)
    part1("") {
        string.trim().split(',').sumOf(::hash)
    }
    part2 {
        val boxes = Array(256) { linkedMapOf<String, String>() }
        for (instr in string.trim().split(',')) {
            if (instr.endsWith('-')) {
                val lens = instr.substring(0, instr.length - 1)
                boxes[hash(lens)].remove(lens)
            } else {
                val (lens, fl) = instr.split('=')
                boxes[hash(lens)][lens] = fl
            }
        }
        log(boxes)
        var totalPower = 0
        for (i in boxes.indices) {
            var slot = 1
            for (lens in boxes[i].keys) {
                val power = (i + 1) * slot++ * boxes[i][lens]!!.toInt()
                log("$lens: $power")
                totalPower += power
            }
        }
        totalPower
    }
}