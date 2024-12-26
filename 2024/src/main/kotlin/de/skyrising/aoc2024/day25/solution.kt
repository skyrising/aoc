@file:PuzzleName("Code Chronicle")

package de.skyrising.aoc2024.day25

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import de.skyrising.aoc.splitOnEmpty
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

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

fun PuzzleInput.prepare(): Pair<IntList, IntList> {
    val locks = IntArrayList()
    val keys = IntArrayList()
    for (thing in lines.splitOnEmpty()) {
        var p = 0
        for (i in 1..5) {
            for (j in 0..4) {
                p = p shl 1
                if (thing[i][j] == '#') p = p or 1
            }
        }
        (if (thing[0] == "#####") locks else keys).add(p)
    }
    return locks to keys
}

fun Pair<IntList, IntList>.part1(): Int {
    val (locks, keys) = this
    var sum = 0
    for (i in locks.indices) {
        val lock = locks.getInt(i)
        for (j in keys.indices) {
            val key = keys.getInt(j)
            if (key and lock == 0) sum++
        }
    }
    return sum
}
