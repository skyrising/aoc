package de.skyrising.aoc2020.day8

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap

val test = TestInput("""
    nop +0
    acc +1
    jmp +4
    acc +3
    jmp -3
    acc -99
    acc +1
    jmp -4
    acc +6
""")

@PuzzleName("Handheld Halting")
fun PuzzleInput.part1(): Any {
    var acc = 0
    var pc = 0
    val counts = Int2IntOpenHashMap()
    counts.defaultReturnValue(0)
    while (counts[pc] == 0) {
        counts[pc] = counts[pc] + 1
        val (op, arg) = lines[pc].split(" ")
        pc++
        // println("$pc: $op, $arg")
        when (op) {
            "nop" -> {}
            "acc" -> {
                acc += arg.toInt()
            }
            "jmp" -> {
                pc += arg.toInt() - 1
            }
        }
    }
    return acc
}

fun PuzzleInput.part2(): Any? {
    val instrs = mutableListOf<Pair<String, Int>>()
    var bbStart = 0
    val graph = Graph.build<Int, Nothing?> {
        lines.forEachIndexed { i, line ->
            val (op, arg) = line.split(" ")
            val argInt = arg.toInt()
            instrs.add(Pair(op, argInt))
            when (op) {
                "nop" -> {
                    if (bbStart < i) {
                        edge(bbStart, i, 0)
                    }
                    bbStart = i
                    edge(i, i + argInt, 1)
                }
                "jmp" -> {
                    if (bbStart < i) {
                        edge(bbStart, i, 0)
                    }
                    bbStart = i + 1
                    edge(i, i + 1, 1)
                    edge(i, i + argInt, 0)
                }
            }
        }
        val end = instrs.size
        if (bbStart < end - 1) {
            edge(bbStart, end - 1, 0)
        }
    }
    val end = instrs.size
    val path = graph.dijkstra(0, end - 1) ?: return null
    for (edge in path) {
        if (edge.weight <= 0) continue
        val i = edge.from
        // println("Patching $i ${instrs[i]}")
        val instr = instrs[i]
        instrs[i] = Pair(if (instr.first == "nop") "jmp" else "nop", instr.second)
    }
    var acc = 0
    var pc = 0
    while (pc < end) {
        val (op, arg) = instrs[pc]
        pc++
        when (op) {
            "nop" -> {}
            "acc" -> {
                acc += arg
            }
            "jmp" -> {
                pc += arg - 1
            }
        }
    }
    return acc
}
