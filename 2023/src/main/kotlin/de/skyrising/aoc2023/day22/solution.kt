package de.skyrising.aoc2023.day22

import de.skyrising.aoc.*

@Suppress("unused")
class BenchmarkDay : BenchmarkBaseV1(2023, 22)

val test = TestInput("""
    1,0,1~1,2,1
    0,0,2~2,0,2
    0,2,3~2,2,3
    0,0,4~0,2,4
    2,0,5~2,2,5
    0,1,6~2,1,6
    1,1,8~1,1,9
""")

private fun parse(input: PuzzleInput) = input.lines.map {
    val (x1,y1,z1, x2, y2, z2) = it.ints()
    listOf(Vec3i(x1, y1, z1), Vec3i(x2, y2, z2)).boundingBox()
}

private fun letFall(cubes: List<BoundingBox3i>, sorted: Boolean = false): Pair<List<BoundingBox3i>, Int> {
    val newCubes = mutableListOf<BoundingBox3i>()
    var count = 0
    val down = Vec3i(0, 0, -1)
    for (cube in if (sorted) cubes else cubes.sortedBy { it.min.z }) {
        var cube1 = cube
        while (true) {
            if (cube1.min.z == 1) break
            val fallenCube = BoundingBox3i(cube1.min + down, cube1.max + down)
            if (newCubes.any { it.intersects(fallenCube)} ) break
            cube1 = fallenCube
        }
        if (cube1 !== cube) count++
        newCubes.add(cube1)
    }
    return newCubes to count
}

@PuzzleName("Sand Slabs")
fun PuzzleInput.part1(): Any {
    val (cubes, _) = letFall(parse(this))
    return cubes.count {
        letFall(cubes - it, true).second == 0
    }
}

fun PuzzleInput.part2(): Any {
    val (cubes, _) = letFall(parse(this))
    return cubes.sumOf {
        letFall(cubes - it, true).second
    }
}