@file:PuzzleName("Printing Department")

package de.skyrising.aoc2025.day4

import de.skyrising.aoc.PuzzleInput
import de.skyrising.aoc.PuzzleName
import de.skyrising.aoc.TestInput
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt

val test = TestInput("""
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.
""")

fun PuzzleInput.part1(): Int {
    val g = charGrid
    return g.positions.count {
        g[it] == '@' && it.eightNeighbors().count { n -> n in g && g[n] == '@' } < 4
    }
}

fun PuzzleInput.part2(): Int {
    val g = charGrid
    var removed = 0
    val todo = ArrayDeque(g.where { it == '@' })
    while (todo.isNotEmpty()) {
        val p = todo.removeLast()
        if (p !in g) continue
        if (g[p] == '@' && p.eightNeighbors().count { n -> n in g && g[n] == '@' } < 4) {
            g[p] = '.'
            removed++
            todo.addAll(p.eightNeighbors())
        }
    }
    return removed
}

val ROLL_COLOR = 0xffffffu
val FADE_START = 0xafafafu
const val SCALE = 10
const val SPEED = 1

@OptIn(ExperimentalUnsignedTypes::class)
fun PuzzleInput.part2viz() = visualization {
    video = true
    val grid = charGrid
    size = grid.size * SCALE
    val image = BufferedImage(grid.size.x, grid.size.y, BufferedImage.TYPE_INT_RGB)
    val pixels = (image.raster.dataBuffer as DataBufferInt).data.asUIntArray()
    for ((x, y) in grid.positions) {
        pixels[grid.localIndex(x, y)] = if (grid[x, y] == '@') ROLL_COLOR else 0u
    }
    g.drawImage(image, 0, 0, size.x, size.y, null)
    fun present() {
        for (i in pixels.indices) {
            val c = pixels[i] and 0xffu
            if (c in 1u..<0xffu) {
                pixels[i] = 0x10101u * (c - 1u).coerceAtLeast(0u)
            }
        }
        g.drawImage(image, 0, 0, size.x, size.y, null)
        this.present()
    }
    present()
    var removed = 0
    val todo = ArrayDeque(grid.where { it == '@' })
    while (todo.isNotEmpty()) {
        val p = todo.removeLast()
        if (p !in grid) continue
        if (grid[p] == '@' && p.eightNeighbors().count { n -> n in grid && grid[n] == '@' } < 4) {
            grid[p] = '.'
            pixels[grid.localIndex(p.x, p.y)] = FADE_START
            removed++
            if (removed % SPEED == 0) {
                present()
            }
            todo.addAll(p.eightNeighbors())
        }
    }
    repeat((FADE_START and 0xffu).toInt()) { present() }
}