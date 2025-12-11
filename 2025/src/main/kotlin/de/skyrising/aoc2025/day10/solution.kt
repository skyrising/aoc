@file:PuzzleName("Factory")

package de.skyrising.aoc2025.day10

import com.microsoft.z3.ArithExpr
import com.microsoft.z3.Context
import com.microsoft.z3.IntSort
import com.microsoft.z3.Status
import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.ints.IntOpenHashSet

val test = TestInput("""
[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
""")

data class Machine(val lights: Int, val buttons: IntList, val joltage: IntList) {
    fun flipLights(): Int {
        val queue = ArrayDeque<Pair<Int, Int>>()
        val seen = IntOpenHashSet()
        queue.add(0 to 0)
        while (queue.isNotEmpty()) {
            val (f, steps) = queue.removeFirst()
            if (f == lights) return steps
            if (!seen.add(f)) continue
            for (i in buttons.indices) {
                queue.add(f xor buttons.getInt(i) to steps + 1)
            }
        }
        return Int.MAX_VALUE
    }

    fun addJoltage(ctx: Context) = ctx.run {
        val solver = mkOptimize()
        val buttonPresses = Array<ArithExpr<IntSort>>(buttons.size) {
            mkIntConst("b$it").also { btn -> solver += btn ge 0 }
        }
        for (joltIdx in joltage.indices) {
            solver += (buttonPresses.filterIndexed { btnIdx, _ ->
                (buttons.getInt(btnIdx) shr joltIdx).and(1) == 1
            }.reduceOrNull { a, b -> a + b } ?: mkInt(0)) eq joltage.getInt(joltIdx)
        }
        val sum = buttonPresses.reduce { a, b -> a + b }
        solver.MkMinimize(sum)
        if (solver.Check() != Status.SATISFIABLE) error("Not satisfiable")
        solver.model.eval(sum, true).toLong().toInt()
    }
}

fun parseLine(line: String): Machine {
    var i = 1
    var lights = 0
    while (line[i] != ']') {
        if (line[i] == '#') lights = lights or (1 shl (i - 1))
        i++
    }
    i += 2
    val buttons = IntArrayList()
    while (line[i] != '{') {
        var button = 0
        val end = line.indexOf(')', i)
        for (j in line.substring(i, end).ints().intIterator()) button = button or (1 shl j)
        buttons.add(button)
        i = end + 2
    }
    return Machine(lights, buttons, line.substring(i).ints())
}

fun PuzzleInput.prepare() = lines.map(::parseLine)
fun List<Machine>.part1() = sumOf(Machine::flipLights)
fun List<Machine>.part2() = Context().use { ctx -> sumOf { it.addJoltage(ctx) } }
