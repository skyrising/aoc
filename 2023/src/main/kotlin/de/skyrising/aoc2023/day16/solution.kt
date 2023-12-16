package de.skyrising.aoc2023.day16

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 16)

data class State(val pos: Vec2i, val dir: Vec2i)

tailrec fun CharGrid.traverseBeam(pos: Vec2i, dir: Vec2i, beam: MutableSet<State>) {
    if (pos in this && !beam.add(State(pos, dir))) return
    val newPos = pos + dir
    if (newPos !in this) return
    when (this[newPos]) {
        '.' -> traverseBeam(newPos, dir, beam)
        '/' -> when (dir) {
            Vec2i.N -> traverseBeam(newPos, Vec2i.E, beam)
            Vec2i.E -> traverseBeam(newPos, Vec2i.N, beam)
            Vec2i.S -> traverseBeam(newPos, Vec2i.W, beam)
            Vec2i.W -> traverseBeam(newPos, Vec2i.S, beam)
        }
        '\\' -> when (dir) {
            Vec2i.N -> traverseBeam(newPos, Vec2i.W, beam)
            Vec2i.E -> traverseBeam(newPos, Vec2i.S, beam)
            Vec2i.S -> traverseBeam(newPos, Vec2i.E, beam)
            Vec2i.W -> traverseBeam(newPos, Vec2i.N, beam)
        }
        '-' -> if (dir == Vec2i.N || dir == Vec2i.S){
            traverseBeam(newPos, Vec2i.W, beam)
            traverseBeam(newPos, Vec2i.E, beam)
        } else {
            traverseBeam(newPos, dir, beam)
        }
        '|' -> if (dir == Vec2i.E || dir == Vec2i.W) {
            traverseBeam(newPos, Vec2i.N, beam)
            traverseBeam(newPos, Vec2i.S, beam)
        } else {
            traverseBeam(newPos, dir, beam)
        }
    }
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
        val beam = linkedSetOf<State>()
        grid.traverseBeam(Vec2i(-1, 0), Vec2i.E, beam)
        beam.mapTo(mutableSetOf()) { it.pos }.size
    }
    part2 {
        val grid = charGrid
        fun count(pos: Vec2i, dir: Vec2i): Int {
            val beam = linkedSetOf<State>()
            grid.traverseBeam(pos, dir, beam)
            return beam.mapTo(mutableSetOf()) { it.pos }.size
        }
        var max = 0
        for (x in 0 until grid.width) {
            max = maxOf(max, count(Vec2i(x, -1), Vec2i.S))
            max = maxOf(max, count(Vec2i(x, grid.height), Vec2i.N))
        }
        for (y in 0 until grid.height) {
            max = maxOf(max, count(Vec2i(-1, y), Vec2i.E))
            max = maxOf(max, count(Vec2i(grid.width, y), Vec2i.W))
        }
        max
    }
}