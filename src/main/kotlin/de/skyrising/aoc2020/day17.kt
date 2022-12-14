package de.skyrising.aoc2020

import de.skyrising.aoc.TestInput
import de.skyrising.aoc.isBitSet
import de.skyrising.aoc.setBit
import it.unimi.dsi.fastutil.ints.IntOpenHashSet

class BenchmarkDay17 : BenchmarkDay(17)

interface StateDelegate {
    val activeCount: Int
    operator fun get(index: Int): Boolean
    operator fun set(index: Int, value: Boolean)
}

private class DenseState(size: Int) : StateDelegate {
    private val state = LongArray((size + 63) / 64)
    override val activeCount get() = state.sumBy { it.countOneBits() }
    override fun get(index: Int) = isBitSet(state, index)
    override fun set(index: Int, value: Boolean) = setBit(state, index, value)
}

private class SparseState : StateDelegate {
    private val state = IntOpenHashSet()
    override val activeCount get() = state.size
    override fun get(index: Int) = index in state
    override fun set(index: Int, value: Boolean) {
        if (value) state.add(index)
        else state.remove(index)
    }
}

private class State3(val sizeX: Int, val sizeY: Int, val sizeZ: Int, val offsetX: Int = 0, val offsetY: Int = 0, val offsetZ: Int = 0, delegator: (Int) -> StateDelegate) {
    private val delegate = delegator(sizeX * sizeY * sizeZ)
    val xRange inline get() = -offsetX until sizeX - offsetX
    val yRange inline get() = -offsetY until sizeY - offsetY
    val zRange inline get() = -offsetZ until sizeZ - offsetZ
    val activeCount inline get() = delegate.activeCount


    fun getIndex(x: Int, y: Int, z: Int): Int {
        if (x + offsetX !in 0 until sizeX) return -1
        if (y + offsetY !in 0 until sizeY) return -1
        if (z + offsetZ !in 0 until sizeZ) return -1
        return x + offsetX + sizeX * (y + offsetY + sizeY * (z + offsetZ))
    }

    operator fun get(x: Int, y: Int, z: Int) = getIndex(x, y, z).let {
        if (it < 0) false else delegate[it]
    }

    operator fun set(x: Int, y: Int, z: Int, value: Boolean) = getIndex(x, y, z).let {
        if (it < 0) throw IndexOutOfBoundsException("x=$x, y=$y, z=$z")
        delegate[it] = value
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (z in zRange) {
            if (z != -offsetZ) sb.append('\n')
            sb.append("z=").append(z).append('\n')
            for (y in yRange) {
                for (x in xRange) {
                    sb.append(if (this[x, y, z]) '#' else '.')
                }
                sb.append('\n')
            }
        }
        return sb.toString()
    }
}

private class State4(val sizeX: Int, val sizeY: Int, val sizeZ: Int, val sizeW: Int, val offsetX: Int = 0, val offsetY: Int = 0, val offsetZ: Int = 0, val offsetW: Int = 0, delegator: (Int) -> StateDelegate) {
    private val delegate = delegator(sizeX * sizeY * sizeZ * sizeW)
    val xRange inline get() = -offsetX until sizeX - offsetX
    val yRange inline get() = -offsetY until sizeY - offsetY
    val zRange inline get() = -offsetZ until sizeZ - offsetZ
    val wRange inline get() = -offsetW until sizeW - offsetW
    val activeCount inline get() = delegate.activeCount

    private fun getIndex(x: Int, y: Int, z: Int, w: Int): Int {
        if (x + offsetX !in 0 until sizeX) return -1
        if (y + offsetY !in 0 until sizeY) return -1
        if (z + offsetZ !in 0 until sizeZ) return -1
        if (w + offsetW !in 0 until sizeW) return -1
        return x + offsetX + sizeX * (y + offsetY + sizeY * (z + offsetZ + sizeZ * (w + offsetW)))
    }

    operator fun get(x: Int, y: Int, z: Int, w: Int) = getIndex(x, y, z, w).let {
        if (it < 0) false else delegate[it]
    }

    operator fun set(x: Int, y: Int, z: Int, w: Int, value: Boolean) = getIndex(x, y, z, w).let {
        if (it < 0) throw IndexOutOfBoundsException("x=$x, y=$y, z=$z, w=$w")
        delegate[it] = value
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (w in wRange) {
            for (z in zRange) {
                if (w != -offsetW) sb.append('\n')
                sb.append("z=").append(z).append(", w=").append(w).append('\n')
                for (y in yRange) {
                    for (x in xRange) {
                        sb.append(if (this[x, y, z, w]) '#' else '.')
                    }
                    sb.append('\n')
                }
            }
        }
        return sb.toString()
    }
}

private fun cycle(state: State3, delegator: (Int) -> StateDelegate): State3 {
    val newState = State3(
        state.sizeX + 2, state.sizeY + 2, state.sizeZ + 2,
        state.offsetX + 1, state.offsetY + 1, state.offsetZ + 1,
        delegator
    )
    for (z in newState.zRange) {
        for (y in newState.yRange) {
            for (x in newState.xRange) {
                var neighbors = 0
                for (z1 in -1 .. 1) for (y1 in -1 .. 1) for (x1 in -1 .. 1) {
                    if (x1 == 0 && y1 == 0 && z1 == 0) continue
                    if (state[x + x1, y + y1, z + z1]) neighbors++
                }
                if (state[x, y, z]) {
                    newState[x, y, z] = neighbors == 2 || neighbors == 3
                } else {
                    newState[x, y, z] = neighbors == 3
                }
            }
        }
    }
    return newState
}

private fun cycle(state: State4, delegator: (Int) -> StateDelegate): State4 {
    val newState = State4(
        state.sizeX + 2, state.sizeY + 2, state.sizeZ + 2, state.sizeW + 2,
        state.offsetX + 1, state.offsetY + 1, state.offsetZ + 1, state.offsetW + 1,
        delegator
    )
    for (w in newState.wRange) {
        for (z in newState.zRange) {
            for (y in newState.yRange) {
                for (x in newState.xRange) {
                    var neighbors = 0
                    for (w1 in -1 .. 1) for (z1 in -1 .. 1) for (y1 in -1 .. 1) for (x1 in -1 .. 1) {
                        if (x1 == 0 && y1 == 0 && z1 == 0 && w1 == 0) continue
                        if (state[x + x1, y + y1, z + z1, w + w1]) neighbors++
                    }
                    if (state[x, y, z, w]) {
                        newState[x, y, z, w] = neighbors == 2 || neighbors == 3
                    } else {
                        newState[x, y, z, w] = neighbors == 3
                    }
                }
            }
        }
    }
    return newState
}

private fun part1(input: List<String>, delegator: (Int) -> StateDelegate): Int {
    var state = State3(input[0].length, input.size, 1, delegator = delegator)
    input.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            if (c == '#') state[x, y, 0] = true
        }
    }
    //println("Before:")
    //println(state)
    for (c in 1 .. 6) {
        state = cycle(state, delegator)
        //println("After $c cycle${if (c == 1) "" else "s"}:")
        //println(state)
    }
    return state.activeCount
}

private fun part2(input: List<String>, delegator: (Int) -> StateDelegate): Int {
    var state = State4(input[0].length, input.size, 1, 1, delegator = delegator)
    input.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            if (c == '#') state[x, y, 0, 0] = true
        }
    }
    //println("Before:")
    //println(state)
    for (c in 1 .. 6) {
        state = cycle(state, delegator)
        //println("After $c cycle${if (c == 1) "" else "s"}:")
        //println(state)
    }
    return state.activeCount
}

fun registerDay17() {
    val test = TestInput("""
        .#.
        ..#
        ###
    """)
    puzzle(17, "Conway Cubes v1") {
        part1(lines, ::DenseState)
    }
    puzzle(17, "Conway Cubes v2") {
        part1(lines) { SparseState() }
    }
    puzzle(17, "Part 2 v1") {
        part2(lines, ::DenseState)
    }
    puzzle(17, "Part 2 v2") {
        part2(lines) { SparseState() }
    }
}