package de.skyrising.aoc2019

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.ints
import it.unimi.dsi.fastutil.ints.IntList

const val OP_ADD = 1
const val OP_MUL = 2
const val OP_HALT = 99

fun runIntcode(code: IntList): IntList {
    var pc = 0
    while (true) {
        when (code.getInt(pc)) {
            OP_ADD -> {
                code[code.getInt(pc + 3)] = code.getInt(code.getInt(pc + 1)) + code.getInt(code.getInt(pc + 2))
                pc += 4
            }
            OP_MUL -> {
                code[code.getInt(pc + 3)] = code.getInt(code.getInt(pc + 1)) * code.getInt(code.getInt(pc + 2))
                pc += 4
            }
            OP_HALT -> return code
            else -> throw IllegalArgumentException("Invalid opcode ${code.getInt(pc)}")
        }
    }
}

fun runIntcode(input: PuzzleInput) = input.string.ints()