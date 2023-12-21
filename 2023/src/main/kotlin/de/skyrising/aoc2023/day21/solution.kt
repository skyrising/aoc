package de.skyrising.aoc2023.day21

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntList
import it.unimi.dsi.fastutil.longs.LongLinkedOpenCustomHashSet
import it.unimi.dsi.fastutil.longs.LongSets

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 21)

private inline fun CharGrid.goOutwards(steps: Int, cb: (Int,Int)->Unit = { _, _ -> }): Int {
    var front = LongSets.singleton(PackedIntPair.pack(where { it == 'S' }.single()))
    val count = intArrayOf(1, 0)
    val seen = Array(2) { LongSets.emptySet() }
    cb(0, 1)
    for (i in 1..steps) {
        val j = i and 1
        seen[j] = front
        val newFront = LongLinkedOpenCustomHashSet(front.size + 50, PackedIntPair.HASH_STRATEGY)
        val iter = front.longIterator()
        while (iter.hasNext()) for (n in PackedIntPair(iter.nextLong()).toVec2i().fourNeighbors()) {
            val l = PackedIntPair.pack(n)
            if (l !in seen[j xor 1] && this[n.x.mod(width), n.y.mod(height)] != '#') newFront.add(l)
        }
        front = newFront
        count[j] += front.size
        cb(i, count[j])
    }
    return count[steps and 1]
}

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
        charGrid.goOutwards(64)
    }
    part2 {
        val g = charGrid
        val steps = 26501365
        val size = g.width
        val rem = steps % size
        val values = IntArray(3)
        g.goOutwards(size * 2 + rem) { i, v ->
            if (i % size == rem) values[i / size] = v
        }
        gregoryNewtonExtrapolation(differenceCoefficients(IntList.of(*values)), steps / size)
    }
}