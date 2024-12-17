package de.skyrising.aoc2024.day17

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.longs.LongList

val test = TestInput("""
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
""")

data class State(var a: Long, var b: Long, var c: Long, var pc: Int = 0, val output: IntList = IntArrayList()) {
    fun getCombo(combo: Int) = when (combo) {
        in 0..3 -> combo.toLong()
        4 -> a
        5 -> b
        6 -> c
        else -> throw IllegalArgumentException("Invalid combo: $combo")
    }

    fun run(instruction: Instruction): State {
        instruction(this)
        pc++
        return this
    }

    fun run(instructions: List<Instruction>): State {
        while (pc < instructions.size) {
            val instr = instructions[pc]
            run(instr)
        }
        return this
    }

    override fun toString() = "@$pc, A=$a, B=$b, C=$c \"${output.joinToString(",")}\""
}

sealed interface Instruction {
    operator fun invoke(state: State): Unit
    data class Adv(val combo: Int) : Instruction {
        override fun invoke(state: State) {
            state.a = state.a shr state.getCombo(combo).toInt()
        }
    }
    data class Bxl(val literal: Int) : Instruction {
        override fun invoke(state: State) {
            state.b = state.b xor literal.toLong()
        }
    }
    data class Bst(val combo: Int) : Instruction {
        override fun invoke(state: State) {
            state.b = state.getCombo(combo) and 7
        }
    }
    data class Jnz(val literal: Int) : Instruction {
        override fun invoke(state: State) {
            if (state.a != 0L) state.pc = literal / 2 - 1
        }
    }
    data object Bxc : Instruction {
        override fun invoke(state: State) {
            state.b = state.b xor state.c
        }
    }
    data class Out(val combo: Int) : Instruction {
        override fun invoke(state: State) {
            state.output.add(state.getCombo(combo).toInt() and 7)
        }
    }
    data class Bdv(val combo: Int) : Instruction {
        override fun invoke(state: State) {
            state.b = state.a shr state.getCombo(combo).toInt()
        }
    }
    data class Cdv(val combo: Int) : Instruction {
        override fun invoke(state: State) {
            state.c = state.a shr state.getCombo(combo).toInt()
        }
    }
    companion object {
        val INSTRUCTIONS = listOf(::Adv, ::Bxl, ::Bst, ::Jnz, { Bxc }, ::Out, ::Bdv, ::Cdv)
        fun parse(input: IntList) = input.chunked(2) { (op, arg) -> INSTRUCTIONS[op](arg) }
    }
}

fun parseInput(input: PuzzleInput): Pair<State, List<Instruction>> {
    return State(
        input.lines[0].longs().getLong(0),
        input.lines[1].longs().getLong(0),
        input.lines[2].longs().getLong(0),
        0) to Instruction.parse(input.lines[4].ints())
}

@PuzzleName("Chronospatial Computer")
fun PuzzleInput.part1(): Any {
    val (state, instructions) = parseInput(this)
    state.run(instructions)
    return state.output.joinToString(",")
}

fun PuzzleInput.part2(): Any {
    val (_, instructions) = parseInput(this)
    val filtered = instructions.filterNot { it == Instruction.Adv(3) }
    require(filtered[0] == Instruction.Bst(4))
    require(filtered[1] is Instruction.Bxl)
    require(filtered[2] == Instruction.Cdv(5))
    require(filtered[3] == Instruction.Bxc)
    require(filtered[4] is Instruction.Bxl)
    require(filtered[5] == Instruction.Out(5))
    require(filtered[6] == Instruction.Jnz(0))

    // Inputs have a consistent structure (modulo the adv position)
    //  2, 4,  1, x,  7, 5, 4, 1,  1, y,  5, 5,  0, 3,  3, 0
    // bst a, bxl x, cdv b,  bxc, bxl y, out b, adv 3, jnz 0

    //  2, 4,  1, x,  7, 5, 4, 7,  0, 3,  1, y,  5, 5,  3, 0
    // bst a, bxl x, cdv b,  bxc, adv 3, bxl y, out b, jnz 0

    // Which is equivalent to
    // while a > 0:
    //   b = (a ^ x) & 7
    //   c = (a >> b) & 7
    //   out(b ^ c ^ y)
    //   a = a >> 3

    fun reverse(output: IntList, current: Long, x: Int, y: Int): LongList {
        if (output.isEmpty()) return LongList.of(current)
        val target = output.getInt(output.lastIndex) xor y
        val result = LongArrayList()
        for (i in 0..7) {
            val b = i xor x
            val aFragment = (current shl 3) or b.toLong()
            val c = i xor target
            if ((aFragment shr i) and 7 != c.toLong()) continue
            result.addAll(reverse(output.subList(0, output.size - 1), aFragment, x, y))
        }
        return result
    }

    val (x, y) = instructions.filterIsInstance<Instruction.Bxl>().map { it.literal }
    val reversed = reverse(IntList.of(2, 4, 1, 3, 7, 5, 4, 7, 0, 3, 1, 5, 5, 5, 3, 0), 0, x, y)
    val solution = reversed.min()
    /*
    var a = solution
    while (a > 0) {
        var b = a xor x.toLong() and 7
        val c = a shr b.toInt() and 7
        b = b xor c
        println("${(b xor y.toLong()) and 7} $b $c ${a.toString(2)}")
        a = a shr 3
    }
    println(state.copy(a = solution, output = IntArrayList()).run(instructions))
    println(lines[4])
    */
    return solution
}
