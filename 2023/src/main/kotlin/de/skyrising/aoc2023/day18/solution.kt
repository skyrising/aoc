package de.skyrising.aoc2023.day18

import de.skyrising.aoc.*
import kotlin.math.absoluteValue

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 18)

@Suppress("unused")
fun register() {
    val test = TestInput("""
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """)
    part1("") {
        var pos = Vec2i.ZERO
        var area = 0
        var perimeter = 0
        for (line in lines) {
            val (dir, distS) = line.split(' ')
            val dist = distS.toInt()
            perimeter += dist
            val nextPos = pos + Vec2i.KNOWN[dir]!! * dist
            area += pos.x * nextPos.y - pos.y * nextPos.x
            pos = nextPos
        }
        area = area.absoluteValue / 2
        area + perimeter / 2 + 1
    }
    part2 {
        var posX = 0L
        var posY = 0L
        var area = 0L
        var perimeter = 0L
        for (line in lines) {
            val (_, _, colorS) = line.split(' ')
            val colors = colorS.substring(2, 8).toInt(16)
            val dist = colors shr 4
            val dir = Direction((colors + 1) and 3)
            perimeter += dist
            val nextPosX = posX + dir.x * dist
            val nextPosY = posY + dir.y * dist
            area += posX * nextPosY - posY * nextPosX
            posX = nextPosX
            posY = nextPosY
        }
        area = area.absoluteValue / 2
        area + perimeter / 2 + 1
    }
}