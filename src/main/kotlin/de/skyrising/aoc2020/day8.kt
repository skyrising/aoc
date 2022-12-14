package de.skyrising.aoc2020

import de.skyrising.aoc.Graph
import de.skyrising.aoc.TestInput
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap

class BenchmarkDay8 : BenchmarkDayV1(8)

fun registerDay8() {
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
    puzzle(8, "Handheld Halting v1") {
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
        acc
    }
    puzzle(8, "Part 2 v1") {
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
        val path = graph.dijkstra(graph[0]!!, graph[end - 1]!!) ?: return@puzzle null
        for (edge in path) {
            if (edge.weight <= 0) continue
            val i = edge.from.value
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
        acc
    }
}