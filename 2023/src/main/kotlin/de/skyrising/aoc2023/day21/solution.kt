package de.skyrising.aoc2023.day21

import de.skyrising.aoc.BenchmarkBaseV1
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.part1
import de.skyrising.aoc.part2
import it.unimi.dsi.fastutil.ints.IntArrayList

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 21)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        ...........
        .....###.#.
        .###.##..#.
        ..#.#...#..
        ....#.#....
        .##..S####.
        .##..#...#.
        .......##..
        .##.#.####.
        .##..##.##.
        ...........
    """)
    part1("Step Counter") {
        val g = charGrid
        var set = g.where { it == 'S' }.toSet()
        repeat(64) {
            set = set.flatMapTo(mutableSetOf()) { it.fourNeighbors().filter { g[it] != '#' } }.toSet()
        }
        set.size
    }
    part2 {
        val g = charGrid
        val start = g.where { it == 'S' }.first()
        val steps = 26501365
        val rem = steps % g.width
        var reachable = setOf(start)
        val values = IntArrayList()
        repeat(g.width * 2 + rem) {
            reachable = reachable.flatMapTo(mutableSetOf()) { it.fourNeighbors().filter { g[it.x.mod(g.width), it.y.mod(g.height)] != '#' } }
            if ((it + 1) % g.width == rem) {
                values.add(reachable.size)
            }
        }
        val d1 = values.getInt(1) - values.getInt(0)
        val d2 = values.getInt(2) - values.getInt(1)
        val d12 = d2 - d1
        fun f(n: Long) = values.getInt(0) + n * d1 + n * (n - 1) / 2 * d12
        f((steps / g.width).toLong())
    }
}