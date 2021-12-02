package de.skyrising.aoc2021

class BenchmarkDay2 : BenchmarkDayV1(2)

fun registerDay2() {
    puzzleLS(2, "Dive!") {
        val test = listOf("forward 5", "down 5", "forward 8", "up 3", "down 8", "forward 2")
        var horizontal = 0
        var depth = 0
        for (line in it) {
            val (cmd, num) = line.split(' ', limit = 2)
            when (cmd) {
                "forward" -> horizontal += num.toInt()
                "down" -> depth += num.toInt()
                "up" -> depth -= num.toInt()
            }
        }
        depth * horizontal
    }
    puzzleLS(2, "Part Two") {
        val test = listOf("forward 5", "down 5", "forward 8", "up 3", "down 8", "forward 2")
        var horizontal = 0
        var depth = 0
        var aim = 0
        for (line in it) {
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
        depth * horizontal
    }
}