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
    Cube(listOf(Vec3i(x1, y1, z1), Vec3i(x2, y2, z2)).boundingBox())
}

private fun letFall(cubes: List<Cube>, shortCircuit: Boolean = false): Pair<List<Cube>, Int> {
    val newCubes = ArrayList<Cube>(cubes.size)
    var count = 0
    var maxZ = 0
    for (oldCube in cubes.sortedBy { it.z }) {
        val cube = oldCube.copy()
        cube.z = minOf(maxZ + 1, cube.z)
        while (cube.z > 1) {
            cube.z--
            if (newCubes.any { it.maxZ >= cube.z && it.maxX >= cube.x && it.x <= cube.maxX && it.intersects(cube)} ) {
                cube.z++
                break
            }
        }
        if (cube.z < oldCube.z) {
            count++
            if (shortCircuit) return newCubes to count
        }
        newCubes.add(cube)
        maxZ = maxOf(cube.maxZ, maxZ)
    }
    return newCubes to count
}

@PuzzleName("Sand Slabs")
fun PuzzleInput.part1(): Any {
    val (cubes, _) = letFall(parse(this))
    return cubes.count {
        letFall(cubes - it, shortCircuit = true).second == 0
    }
}

fun PuzzleInput.part2(): Any {
    val (cubes, _) = letFall(parse(this))
    return cubes.sumOf {
        letFall(cubes - it).second
    }
}