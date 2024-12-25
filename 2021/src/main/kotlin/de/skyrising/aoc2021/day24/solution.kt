@file:PuzzleName("Arithmetic Logic Unit")

package de.skyrising.aoc2021.day24

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.util.function.IntFunction

fun PuzzleInput.part1() = solve(this, intArrayOf(9, 8, 7, 6, 5, 4, 3, 2, 1))

fun PuzzleInput.part2() = solve(this, intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9))

data class Box<T>(val value: T)

private const val CACHE_SIZE = 192
class IntIntCache<T> {
    private val cache = Int2ObjectOpenHashMap<Int2ObjectLinkedOpenHashMap<Box<T>>>()

    inline fun eval(a: Int, b: Int, fn: (Int, Int) -> T): T {
        val v = this[a, b]
        if (v != null) return v.value
        val value = fn(a, b)
        this[a, b] = value
        return value
    }

    operator fun set(a: Int, b: Int, value: T) {
        val level1 = cache.computeIfAbsent(a, IntFunction { Int2ObjectLinkedOpenHashMap(CACHE_SIZE + 1) })
        level1.putAndMoveToFirst(b, Box(value))
        if (level1.size > CACHE_SIZE) level1.removeLast()
    }
    operator fun get(a: Int, b: Int): Box<T>? = cache[a]?.getAndMoveToFirst(b)
}

fun solve(input: PuzzleInput, order: IntArray): String? {
    val cache = IntIntCache<String?>()
    return solveRecursive(input.lines.map {
        val parts = it.split(' ')
        AluInstr(parts[0], *parts.subList(1, parts.size).map(::AluSymbol).toTypedArray())
    }, cache, order, 0, 0)
}

data class AluSymbol(val value: String) {
    val intValue = value.toIntOrNull()
}
class AluInstr(op: String, private vararg val args: AluSymbol) {
    val op = op[1]
    operator fun get(arg: Int) = args[arg]
}

fun solveRecursive(instr: List<AluInstr>, cache: IntIntCache<String?>, order: IntArray, step: Int, z: Int): String? {
    for (input in order) {
        val state = State(0, 0, z, 0)
        state[instr[step][0].value] = input
        var i = step + 1
        while (true) {
            if (i == instr.size) {
                if (state.z == 0) return input.toString()
                break
            }
            val ins = instr[i]
            when (ins.op) {
                // iNp
                'n' -> {
                    val r = cache.eval(i, state.z) { a, b -> solveRecursive(instr, cache, order, a, b) }
                    if (r != null) return "$input$r"
                    break
                }
                // aDd
                'd' -> state[ins[0].value] = state[ins[0]] + state[ins[1]]
                // mUl
                'u' -> state[ins[0].value] = state[ins[0]] * state[ins[1]]
                // dIv
                'i' -> state[ins[0].value] = state[ins[0]] / state[ins[1]]
                // mOd
                'o' -> state[ins[0].value] = state[ins[0]] % state[ins[1]]
                // eQl
                'q' -> state[ins[0].value] = if (state[ins[0]] == state[ins[1]]) 1 else 0
            }
            i++
        }
    }
    return null
}

data class State(var x: Int = 0, var y: Int = 0, var z: Int = 0, var w: Int = 0) {
    operator fun get(index: AluSymbol): Int {
        val intValue = index.intValue
        if (intValue != null) return intValue
        return when (index.value[0]) {
            'x' -> x
            'y' -> y
            'z' -> z
            'w' -> w
            else -> throw IllegalArgumentException(index.value)
        }
    }
    operator fun set(index: String, value: Int) {
        when (index[0]) {
            'x' -> x = value
            'y' -> y = value
            'z' -> z = value
            'w' -> w = value
        }
    }
}
