package de.skyrising.aoc2023

import de.skyrising.aoc.TestInput

class BenchmarkDay2 : BenchmarkDayV1(2)

fun registerDay2() {
    val test = TestInput("""
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """)
    puzzle(2, "Cube Conundrum") {
        var possibleSum = 0
        outer@for (game in lines) {
            val (gameNum, gameData) = game.split(": ")
            val rounds = gameData.split("; ")
            for (round in rounds) {
                var red = 12
                var green = 13
                var blue = 14
                for (reveal in round.split(", ")) {
                    val (count, color) = reveal.split(" ")
                    when (color) {
                        "red" -> red -= count.toInt()
                        "green" -> green -= count.toInt()
                        "blue" -> blue -= count.toInt()
                        else -> error("Invalid color: $color")
                    }
                }
                if (red < 0 || green < 0 || blue < 0) continue@outer
            }
            possibleSum += gameNum.split(" ")[1].toInt()
        }
        possibleSum
    }
    puzzle(2, "Part Two") {
        var sumPowers = 0
        outer@for (game in lines) {
            val (gameNum, gameData) = game.split(": ")
            val rounds = gameData.split("; ")
            var maxRed = 0
            var maxGreen = 0
            var maxBlue = 0
            for (round in rounds) {
                var red = 0
                var green = 0
                var blue = 0
                for (reveal in round.split(", ")) {
                    val (count, color) = reveal.split(" ")
                    when (color) {
                        "red" -> red += count.toInt()
                        "green" -> green += count.toInt()
                        "blue" -> blue += count.toInt()
                        else -> error("Invalid color: $color")
                    }
                }
                maxRed = maxRed.coerceAtLeast(red)
                maxGreen = maxGreen.coerceAtLeast(green)
                maxBlue = maxBlue.coerceAtLeast(blue)
            }
            sumPowers += maxRed * maxGreen * maxBlue
        }
        sumPowers
    }
}
        