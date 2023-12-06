package de.skyrising.aoc2019

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.ints
import it.unimi.dsi.fastutil.ints.IntList

const val OP_ADD = 1
const val OP_MUL = 2
const val OP_HALT = 99

class Instruction(private val code: IntList, private val offset: Int) {
    operator fun get(off: Int) = code.getInt(offset + off + 1)
    val opcode get() = code.getInt(offset)
    operator fun component1() = this[0]
    operator fun component2() = this[1]
    operator fun component3() = this[2]

    val length get() = when (opcode) {
        OP_ADD -> 4
        OP_MUL -> 4
        OP_HALT -> 1
        else -> throw IllegalArgumentException("Invalid opcode $opcode")
    }

    val next get() = Instruction(code, offset + length)
}

fun runIntcode(code: IntList): IntList {
    var pc = Instruction(code, 0)
    while (true) {
        when (pc.opcode) {
            OP_ADD -> code[pc[2]] = code.getInt(pc[0]) + code.getInt(pc[1])
            OP_MUL -> code[pc[2]] = code.getInt(pc[0]) * code.getInt(pc[1])
            OP_HALT -> return code
            else -> throw IllegalArgumentException("Invalid opcode ${pc.opcode}")
        }
        pc = pc.next
    }
}

fun runIntcode(input: PuzzleInput, modify: (IntList)->Unit = {}) = runIntcode(input.string.ints().also(modify))