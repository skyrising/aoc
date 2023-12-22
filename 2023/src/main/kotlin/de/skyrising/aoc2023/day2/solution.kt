package de.skyrising.aoc2023.day2

import de.skyrising.aoc.*

val test = TestInput("""
    Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
    Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
    Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
    Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
    Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
""")
data class Round(val red: Int, val green: Int, val blue: Int)
data class Game(val num: Int, val rounds: List<Round>)
fun parse(game: String): Game {
    val (gameNum, gameData) = game.split(": ")
    return Game(gameNum.split(" ")[1].toInt(), gameData.split("; ").map {
        var red = 0
        var green = 0
        var blue = 0
        for (reveal in it.split(", ")) {
            val (count, color) = reveal.split(" ")
            when (color) {
                "red" -> red += count.toInt()
                "green" -> green += count.toInt()
                "blue" -> blue += count.toInt()
                else -> error("Invalid color: $color")
            }
        }
        Round(red, green, blue)
    })
}

@PuzzleName("Cube Conundrum")
fun PuzzleInput.part1() = lines.map(::parse).sumOf {
    if (it.rounds.all { round -> round.red <= 12 && round.green <= 13 && round.blue <= 14 }) it.num else 0
}

fun PuzzleInput.part2() = lines.map(::parse).sumOf {
    it.rounds.maxOf(Round::red) * it.rounds.maxOf(Round::green) * it.rounds.maxOf(Round::blue)
}

        