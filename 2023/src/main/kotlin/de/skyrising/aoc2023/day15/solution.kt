package de.skyrising.aoc2023.day15

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap

fun hash(s: String) = s.fold(0) { a, b -> (a + b.code) * 17} and 0xff

val test = TestInput("""
    rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
""")

@PuzzleName("Lens Library")
fun PuzzleInput.part1(): Any {
    return string.trim().split(',').sumOf(::hash)
}

fun PuzzleInput.part2(): Any {
    val boxes = Array(256) { Object2IntLinkedOpenHashMap<String>() }
    for (instr in string.trim().split(',')) {
        if (instr.endsWith('-')) {
            val lens = instr.substring(0, instr.length - 1)
            boxes[hash(lens)].removeInt(lens)
        } else {
            val (lens, fl) = instr.split('=')
            boxes[hash(lens)].put(lens, fl.toInt())
        }
    }
    var totalPower = 0
    boxes.forEachIndexed { i, box ->
        box.values.forEachIndexed { slot, lens ->
            totalPower += (i + 1) * (slot + 1) * lens
        }
    }
    return totalPower
}
