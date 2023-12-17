package de.skyrising.aoc2023.day16

import de.skyrising.aoc.*
import java.util.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 16)

@JvmInline
value class State(val intValue: Int) {
    constructor(x: Int, y: Int, dir: Direction): this(((x and 0x7fff) shl 15) or (y and 0x7fff) or (dir.ordinal shl 30))

    val x inline get() = intValue shl 2 shr 17
    val y inline get() = intValue shl 17 shr 17
    val dir inline get() = Direction(intValue ushr 30)
    inline operator fun component1() = x
    inline operator fun component2() = y
    inline operator fun component3() = dir
}

fun CharGrid.traverseBeam(state: State, beam: BitSet, positions: BitSet, deque: IntArrayDeque) {
    deque.enqueue(state.intValue)
    while (!deque.isEmpty) {
        var (x, y, dir) = State(deque.dequeueLastInt())
        while (true) {
            if (x >= 0 && y >= 0 && x < width && y < height) {
                val beamIndex = localIndex(x, y) shl 2 or dir.ordinal
                val seen = beam.get(beamIndex)
                beam.set(beamIndex)
                if (seen) break
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

inline fun CharGrid.maxBeam(posX: Int, posY: Int, dir: Direction, beam: BitSet, positions: BitSet, deque: IntArrayDeque): Int {
    beam.clear()
    positions.clear()
    deque.clear()
    traverseBeam(State(posX, posY, dir), beam, positions, deque)
    return positions.cardinality()
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
        val grid = charGrid
        grid.maxBeam(
            -1,
            0,
            Direction.E,
            BitSet(grid.width * grid.height * 4),
            BitSet(grid.width * grid.height),
            IntArrayDeque(128)
        )
    }
    part2 {
        val grid = charGrid
        var max = 0
        val beam = BitSet(grid.width * grid.height * 4)
        val positions = BitSet(grid.width * grid.height)
        val deque = IntArrayDeque(128)
        for (x in 0 until grid.width) {
            max = maxOf(max, grid.maxBeam(x, -1, Direction.S, beam, positions, deque))
            max = maxOf(max, grid.maxBeam(x, grid.height, Direction.N, beam, positions, deque))
        }
        for (y in 0 until grid.height) {
            max = maxOf(max, grid.maxBeam(-1, y, Direction.E, beam, positions, deque))
            max = maxOf(max, grid.maxBeam(grid.width, y, Direction.W, beam, positions, deque))
        }
        max
    }
}