package de.skyrising.aoc2022.day17

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import kotlin.math.max

private fun collides(rock: List<Vec2i>, pos: Vec2i, landed: Set<Vec2i>): Boolean {
    for (p in rock) {
        val p1 = p + pos
        if (p1.x < 0 || p1.x >= 7 || p1.y < 0 || p1 in landed) return true
    }
    return false
}

private fun printPit(landed: Set<Vec2i>, moving: Collection<Vec2i>? = null) {
    if (landed.isEmpty() && moving.isNullOrEmpty()) return
    var bbox = landed.boundingBox().expand(Vec2i(0, 0)).expand(Vec2i(6, 0))
    if (!moving.isNullOrEmpty()) bbox = bbox.expand(moving.boundingBox())
    val grid = bbox.charGrid { '.' }
    grid[landed] = '#'
    if (moving != null) grid[moving] = '@'
    for (i in grid.height - 1 downTo 0) {
        print('|')
        for (j in 0 until grid.width) {
            print(grid[j + grid.offset.x, i + grid.offset.y])
        }
        println('|')
    }
    println("+" + "-".repeat(grid.width) + "+")
}

private val rocks = listOf(
    CharGrid(4, 1, "####".toCharArray()),
    CharGrid(3, 3, ".#.###.#.".toCharArray()),
    CharGrid(3, 3, "###..#..#".toCharArray()),
    CharGrid(1, 4, "####".toCharArray()),
    CharGrid(2, 2, "####".toCharArray()),
).map { it.where { c -> c == '#' } }

private fun dropRocks(input: PuzzleInput, rockCount: Long): Long {
    data class Key(val rock: Int, val move: Int, val window: Set<Vec2i>)
    val seen = Object2LongOpenHashMap<Key>()
    seen.defaultReturnValue(-1)
    val heights = IntArrayList()
    val height = IntArray(7)
    val moves = input.chars.mapNotNull { when (it) {
        '>' -> 1
        '<' -> -1
        else -> null
    } }
    val landed = mutableSetOf<Vec2i>()
    var step = 0
    for (i in 0 until rockCount) {
        val rock = rocks[(i % rocks.size).toInt()]
        var pos = Vec2i(2, 3 + (landed.maxOfOrNull { it.y }?:-1) + 1)
        while (true) {
            val side = Vec2i(pos.x + moves[step++ % moves.size], pos.y)
            if (!collides(rock, side, landed)) pos = side
            val down = Vec2i(pos.x, pos.y - 1)
            if (collides(rock, down, landed)) break
            pos = down
        }
        for (p in rock) {
            landed.add(p + pos)
            height[p.x + pos.x] = max(height[p.x + pos.x], p.y + pos.y)
        }
        val maxHeight = height.max()
        heights.add(maxHeight)
        val window = landed.filter { it.y in maxHeight - 9 .. maxHeight }.map { Vec2i(it.x, it.y - maxHeight) }.toSet()
        val key = Key((i % rocks.size).toInt(), step % moves.size, window)
        val prev = seen.put(key, i)
        if (prev >= 0) {
            val cycleLength = i - prev
            val cycleHeight = maxHeight - heights.getInt(prev.toInt())
            val cyclesRemaining = (rockCount - i - 1) / cycleLength
            val offset = (rockCount - i - 1) % cycleLength
            val remainderHeight = heights.getInt((prev + offset).toInt()) - heights.getInt(prev.toInt())
            input.log("Cycle detected at rock $i step $step ($prev -> $i) length: $cycleLength height: $cycleHeight cycles remaining: $cyclesRemaining offset: $offset remainder height: $remainderHeight")
            return maxHeight + cycleHeight * cyclesRemaining + remainderHeight + 1L
        }
        //printPit(landed)
    }
    //printPit(landed)
    return height.max() + 1L
}

val test = TestInput(">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>")
@PuzzleName("Pyroclastic Flow")
fun PuzzleInput.part1() = dropRocks(this, 2022)
fun PuzzleInput.part2() = dropRocks(this, 1000000000000L)
