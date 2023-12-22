package de.skyrising.aoc2021.day10

import de.skyrising.aoc.*
import it.unimi.dsi.fastutil.longs.LongArrayList

val test = TestInput("""
    [({(<(())[]>[[{[]{<()<>>
    [(()[<>])]({[<{<<[]>>(
    {([(<{}[<>[]}>{[]{[(<()>
    (((({<>}<{<{<>}{[]{[]{}
    [[<[([]))<([[{}[[()]]]
    [{[{({}]{}}([{[{{{}}([]
    {<[[]]>}<{[{[{[]{()[[[]
    [<(<(<(<{}))><([]([]()
    <{([([[(<>()){}]>(<<{{
    <{([{{}}[<[[[<>{}]]]>[]]
""")

@PuzzleName("Syntax Scoring")
fun PuzzleInput.part1(): Any {
    var score = 0
    for (line in lines) {
        val stack = ArrayDeque<Char>()
        for (c in line) {
            when (c) {
                '(' -> stack.add(')')
                '[' -> stack.add(']')
                '{' -> stack.add('}')
                '<' -> stack.add('>')
                else -> {
                    if (c != stack.removeLast()) {
                        score += when (c) {
                            ')' -> 3
                            ']' -> 57
                            '}' -> 1197
                            '>' -> 25137
                            else -> throw IllegalArgumentException(c.toString())
                        }
                        break
                    }
                }
            }
        }
    }
    return score
}

fun PuzzleInput.part2(): Any {
    val scores = LongArrayList()
    outer@for (line in lines) {
        val stack = ArrayDeque<Char>()
        for (c in line) {
            when (c) {
                '(' -> stack.add(')')
                '[' -> stack.add(']')
                '{' -> stack.add('}')
                '<' -> stack.add('>')
                else -> {
                    if (c != stack.removeLast()) {
                        continue@outer
                    }
                }
            }
        }
        var score = 0L
        while (stack.isNotEmpty()) {
            score = score * 5 + when (stack.removeLast()) {
                ')' -> 1
                ']' -> 2
                '}' -> 3
                '>' -> 4
                else -> throw IllegalArgumentException()
            }
        }
        scores.add(score)
    }
    scores.sort()
    return scores.getLong(scores.size / 2)
}
