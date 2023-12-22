package de.skyrising.aoc2022.day23

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap

private fun parseInput(input: PuzzleInput): MutableSet<Vec2i> {
    val width = input.lines.maxOf { it.length }
    val height = input.lines.size
    val grid = CharGrid(width, height, CharArray(width * height))
    for (row in 0 until height) {
        val line = input.lines[row]
        for (col in 0 until width) {
            grid[col, row] = if (col < line.length) line[col] else ' '
        }
    }
    return grid.where { it == '#' }.toMutableSet()
}

private fun checkNorth(elf: Vec2i, elves: Set<Vec2i>) = elf.northWest !in elves && elf.north !in elves && elf.northEast !in elves
private fun checkSouth(elf: Vec2i, elves: Set<Vec2i>) = elf.southWest !in elves && elf.south !in elves && elf.southEast !in elves
private fun checkWest(elf: Vec2i, elves: Set<Vec2i>) = elf.northWest !in elves && elf.west !in elves && elf.southWest !in elves
private fun checkEast(elf: Vec2i, elves: Set<Vec2i>) = elf.northEast !in elves && elf.east !in elves && elf.southEast !in elves

private fun propose(elf: Vec2i, elves: Set<Vec2i>, order: Int): Vec2i {
    if (elf.eightNeighbors().none { it in elves }) return elf
    for (i in 0 until 4) {
        when ((i + order) % 4) {
            0 -> if (checkNorth(elf, elves)) return elf.north
            1 -> if (checkSouth(elf, elves)) return elf.south
            2 -> if (checkWest(elf, elves)) return elf.west
            3 -> if (checkEast(elf, elves)) return elf.east
        }
    }
    return elf
}

private fun round(elves: MutableSet<Vec2i>, roundCount: Int): Boolean {
    val proposed = mutableMapOf<Vec2i, Vec2i>()
    val proposedCount = Object2IntOpenHashMap<Vec2i>()
    for (elf in elves) {
        val dest = propose(elf, elves, roundCount)
        proposed[elf] = dest
        proposedCount.addTo(dest, 1)
    }
    var moved = 0
    for ((elf, dest) in proposed) {
        if (proposedCount.getInt(dest) == 1 && dest != elf) {
            elves.remove(elf)
            elves.add(dest)
            moved++
        }
    }
    return moved > 0
}

val test = TestInput("""
    ....#..
    ..###.#
    #...#.#
    .#...##
    #.###..
    ##.#.##
    .#..#..
""")

val test2 = TestInput("""
    .....
    ..##.
    ..#..
    .....
    ..##.
    ..... 
""")

@PuzzleName("Unstable Diffusion")
fun PuzzleInput.part1(): Any {
    val elves = parseInput(this)
    repeat(10) {
        round(elves, it)
    }
    return elves.boundingBox().area - elves.size
}

fun PuzzleInput.part2(): Any {
    val elves = parseInput(this)
    return 1 + countWhile { round(elves, it) }
}
