package de.skyrising.aoc2024.day14

import de.skyrising.aoc.*

val test = TestInput("""
p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3
""")

fun List<String>.getRobots() = map {
    val (px, py, vx, vy) = it.ints()
    Vec2i(px, py) to Vec2i(vx, vy)
}

@PuzzleName("Restroom Redoubt")
fun PuzzleInput.part1(): Any {
    val roomSize = Vec2i(101, 103)
    //val roomSize = Vec2i(11, 7)
    val (startPositions, velocities) = lines.getRobots().unzip()
    val positions = startPositions.mapIndexed { i, pos -> (pos + velocities[i] * 100) mod roomSize }
    val quadrants = LongArray(4)
    for (pos in positions) {
        val quadrant = when {
            pos.x < roomSize.x / 2 && pos.y < roomSize.y / 2 -> 0
            pos.x > roomSize.x / 2 && pos.y < roomSize.y / 2 -> 1
            pos.x < roomSize.x / 2 && pos.y > roomSize.y / 2 -> 2
            pos.x > roomSize.x / 2 && pos.y > roomSize.y / 2 -> 3
            else -> -1
        }
        if (quadrant >= 0) quadrants[quadrant]++
    }
    return quadrants.fold(1L) { acc, i -> acc * i }
}

fun PuzzleInput.part2(): Any {
    val roomSize = Vec2i(101, 103)
    val (startPositions, velocities) = lines.getRobots().unzip()
    val initVarX = startPositions.varianceOf { it.x.toDouble() }
    val initVarY = startPositions.varianceOf { it.y.toDouble() }
    var n = 1
    while (true) {
        val positions = startPositions.mapIndexed { i, pos -> (pos + velocities[i] * n) mod roomSize }
        val varX = positions.varianceOf { it.x.toDouble() }
        val varY = positions.varianceOf { it.y.toDouble() }
        log("n=%d varX=%.1f varY=%.1f".format(n, varX, varY))
        if (varX < initVarX / 2 && varY < initVarY / 2) {
            val grid = CharGrid(roomSize.x, roomSize.y, CharArray(roomSize.x * roomSize.y) { ' ' })
            grid[positions] = '#'
            log(grid)
            return n
        }
        n++
    }
}
