package de.skyrising.aoc2024.day25

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.splitOnEmpty

val test = TestInput("""
#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####
""")

private fun fits(key: IntArray, lock: IntArray): Boolean {
    return key.indices.all { key[it] + lock[it] <= 5 }
}

@PuzzleName("Code Chronicle")
fun PuzzleInput.part1(): Any {
    val locks = mutableListOf<IntArray>()
    val keys = mutableListOf<IntArray>()
    for (thing in lines.splitOnEmpty()) {
        (if (thing[0] == "#####") locks else keys).add(IntArray(5) {
            (1..5).count { i -> thing[i][it] == '#' }
        })
    }
    return locks.sumOf { keys.count { key -> fits(key, it) } }
}
