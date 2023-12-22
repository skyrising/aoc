package de.skyrising.aoc2021.day2

import de.skyrising.aoc.*

val test = TestInput("""
    forward 5
    down 5
    forward 8
    up 3
    down 8
    forward 2
""")

@PuzzleName("Dive!")
fun PuzzleInput.part1(): Any {
    var horizontal = 0
    var depth = 0
    for (line in lines) {
        val (cmd, num) = line.split(' ', limit = 2)
        when (cmd) {
            "forward" -> horizontal += num.toInt()
            "down" -> depth += num.toInt()
            "up" -> depth -= num.toInt()
        }
    }
    return depth * horizontal
}
fun PuzzleInput.part2(): Any {
    var horizontal = 0
    var depth = 0
    var aim = 0
    for (line in lines) {
        val (cmd, num) = line.split(' ', limit = 2)
        when (cmd) {
            "forward" -> {
                horizontal += num.toInt()
                depth += num.toInt() * aim
            }
            "down" -> aim += num.toInt()
            "up" -> aim -= num.toInt()
        }
    }
    return depth * horizontal
}
