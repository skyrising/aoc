package de.skyrising.aoc2023.day16

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue
import java.util.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 16)

@JvmInline
value class State(val intValue: Int) {
    constructor(x: Int, y: Int, dir: Direction): this(((x and 0x7fff) shl 15) or (y and 0x7ffff) or (dir.ordinal shl 30))

    val x inline get() = intValue shl 2 shr 17
    val y inline get() = intValue shl 17 shr 17
    val dir inline get() = Direction(intValue ushr 30)
    inline operator fun component1() = x
    inline operator fun component2() = y
    inline operator fun component3() = dir
}

fun CharGrid.traverseBeam(state: State, beam: BitSet, positions: BitSet) {
    val deque = IntArrayFIFOQueue()
    deque.enqueue(state.intValue)
    while (!deque.isEmpty) {
        var (x, y, dir) = State(deque.dequeueLastInt())
        while (true) {
            if (x >= 0 && y >= 0 && x < width && y < height) {
                val beamIndex = localIndex(x, y) shl 2 or dir.ordinal
                if (beam.get(beamIndex)) break
                beam.set(beamIndex)
            }
            x += dir.x
            y += dir.y
            if (!contains(x, y)) break
            positions.set(localIndex(x, y))
            dir = when (this[x, y]) {
                '.' -> dir
                '/' -> Direction(dir.ordinal xor 1)
                '\\' -> Direction(dir.ordinal.inv() and 3)
                '-' -> if (dir == Direction.N || dir == Direction.S) {
                    deque.enqueue(State(x, y, Direction.W).intValue)
                    Direction.E
                } else {
                    dir
                }
                '|' -> if (dir == Direction.E || dir == Direction.W) {
                    deque.enqueue(State(x, y, Direction.N).intValue)
                    Direction.S
                } else {
                    dir
                }

                else -> error("Invalid character: ${this[x, y]}")
            }
        }
    }
}

inline fun CharGrid.maxBeam(pos: Vec2i, dir: Direction, max: Int, beam: BitSet, positions: BitSet): Int {
    beam.clear()
    positions.clear()
    traverseBeam(State(pos.x, pos.y, dir), beam, positions)
    return maxOf(max, positions.cardinality())
}

@Suppress("unused")
fun register() {
    val test = TestInput("""
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """)
    part1("The Floor Will Be Lava") {
        charGrid.maxBeam(Vec2i(-1, 0), Direction.E, 0, BitSet(), BitSet())
    }
    part2 {
        val grid = charGrid
        var max = 0
        val beam = BitSet()
        val positions = BitSet()
        for (x in 0 until grid.width) {
            max = grid.maxBeam(Vec2i(x, -1), Direction.S, max, beam, positions)
            max = grid.maxBeam(Vec2i(x, grid.height), Direction.N, max, beam, positions)
        }
        for (y in 0 until grid.height) {
            max = grid.maxBeam(Vec2i(-1, y), Direction.E, max, beam, positions)
            max = grid.maxBeam(Vec2i(grid.width, y), Direction.W, max, beam, positions)
        }
        max
    }
}