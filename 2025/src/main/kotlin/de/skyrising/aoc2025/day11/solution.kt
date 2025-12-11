@file:PuzzleName("Reactor")

package de.skyrising.aoc2025.day11

import de.skyrising.aoc.Graph
import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput

val test = TestInput("""
aaa: you hhh
you: bbb ccc
bbb: ddd eee
ccc: ddd eee fff
ddd: ggg
eee: out
fff: out
ggg: out
hhh: ccc fff iii
iii: out
""")

val test2 = TestInput("""
svr: aaa bbb
aaa: fft
fft: ccc
bbb: tty
tty: ccc
ccc: ddd eee
ddd: hub
hub: fff
eee: dac
dac: fff
fff: ggg hhh
ggg: out
hhh: out
""")

fun PuzzleInput.prepare(): Graph<String, Unit> = Graph.build {
    for (line in lines) {
        val nodes = line.split(' ')
        val first = nodes[0].substring(0, nodes[0].length - 1)
        for (i in 1 until nodes.size) {
            edge(first, nodes[i], 1, null)
        }
    }
}

fun Graph<String, *>.part1() = countPaths("you", "out")

fun Graph<String, *>.part2() =
    countPaths("svr", "dac") * countPaths("dac", "fft") * countPaths("fft", "out") +
    countPaths("svr", "fft") * countPaths("fft", "dac") * countPaths("dac", "out")
